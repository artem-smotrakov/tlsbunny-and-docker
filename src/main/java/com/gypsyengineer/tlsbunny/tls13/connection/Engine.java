package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.connection.action.ActionFailed;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;
import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.ECDHENegotiator;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Engine {

    private static final ByteBuffer NOTHING = ByteBuffer.allocate(0);

    private enum ActionType {
        send, receive, receive_if, allow, run
    }

    public enum Status {
        not_started, running, could_not_send, unexpected_error, success
    }

    private final List<ActionHolder> actions = new ArrayList<>();

    private Output output = new Output();
    private String host = "localhost";
    private int port = 443;
    private Status status = Status.not_started;
    private ByteBuffer buffer = NOTHING;
    private ByteBuffer applicationData = NOTHING;
    private Context context = new Context();

    // timeout for reading incoming data (in millis)
    private long timeout = Connection.DEFAULT_READ_TIMEOUT;

    // if true, always check for CCS after receiving data
    private boolean checkForCCS = false;

    // if true, then stop if an alert occurred
    private boolean stopIfAlert = true;

    // if true, then an exception is thrown in case of error
    private boolean strict = false;

    // this is a label to mark a particular connection
    private String label = String.format("connection:%d", System.currentTimeMillis());

    private Engine() {
        context.group = NamedGroup.secp256r1;
        context.scheme = SignatureScheme.ecdsa_secp256r1_sha256;
        context.suite = CipherSuite.TLS_AES_128_GCM_SHA256;
        context.factory = StructFactory.getDefault();
    }

    public Engine target(String host) {
        this.host = host;
        return this;
    }

    public Engine target(int port) {
        this.port = port;
        return this;
    }

    public Engine timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public Engine strict() {
        this.strict = true;
        return this;
    }

    public Engine continueIfAlert() {
        stopIfAlert = false;
        return this;
    }

    public Engine checkForCCS() {
        checkForCCS = true;
        return this;
    }

    public Engine label(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.label = label;
        return this;
    }

    public Engine set(Output output) {
        this.output = output;
        return this;
    }

    public Engine set(StructFactory factory) {
        this.context.factory = factory;
        return this;
    }

    public Engine set(SignatureScheme scheme) {
        this.context.scheme = scheme;
        return this;
    }

    public Engine set(NamedGroup group) {
        this.context.group = group;
        return this;
    }

    public Engine set(Negotiator negotiator) {
        this.context.negotiator = negotiator;
        return this;
    }

    public Engine send(Action action) {
        actions.add(new ActionHolder(() -> action, ActionType.send));
        return this;
    }

    public Engine send(int n, ActionFactory factory) {
        for (int i=0; i<n; i++) {
            send(factory.create());
        }
        return this;
    }

    public Engine receive(Action action) {
        actions.add(new ActionHolder(() -> action, ActionType.receive));
        return this;
    }

    public Engine receive(Condition condition, ActionFactory factory) {
        actions.add(new ActionHolder(factory, ActionType.receive_if).set(condition));
        return this;
    }

    public Engine allow(Action action) {
        actions.add(new ActionHolder(() -> action, ActionType.allow));
        return this;
    }

    public Engine run(Action action) {
        actions.add(new ActionHolder(() -> action, ActionType.run));
        return this;
    }

    public Engine connect() throws EngineException {
        context.negotiator.set(output);
        status = Status.running;
        try (Connection connection = Connection.create(host, port, timeout)) {
            buffer = NOTHING;

            loop:
            for (ActionHolder holder : actions) {
                ActionFactory actionFactory = holder.factory;

                Action action;
                switch (holder.type) {
                    case send:
                        try {
                            action = actionFactory.create();
                            output.info("send: %s", action.name());
                            init(action).run();
                            connection.send(action.out());
                        } catch (ActionFailed | AEADException | NegotiatorException | IOException e) {
                            status = Status.could_not_send;
                            return reportError(e);
                        }
                        break;
                    case receive:
                        try {
                            action = actionFactory.create();
                            output.info("receive: %s", action.name());
                            read(connection, action);
                            init(action).run();
                            combineData(action);
                        } catch (ActionFailed | AEADException | NegotiatorException | IOException e) {
                            status = Status.unexpected_error;
                            return reportError(e);
                        }
                        break;
                    case receive_if:
                        try {
                            action = actionFactory.create();
                            while (holder.condition.met(context)) {
                                output.info("receive (conditional): %s", action.name());
                                read(connection, action);
                                init(action).run();
                                combineData(action);
                            }
                        } catch (ActionFailed | AEADException | NegotiatorException | IOException e) {
                            status = Status.unexpected_error;
                            return reportError(e);
                        }
                        break;
                    case allow:
                        // TODO: if an action decrypts out, but the action fails,
                        //       then decryption in the next action is going to fail
                        //       this may be fixed by propagating decrypted out
                        //       to the next action
                        // TODO: deprecate `allow`
                        action = actionFactory.create();
                        try {
                            read(connection, action);
                            buffer.mark();
                            output.info("allow: %s", action.name());
                            init(action).run();
                            combineData(action);
                        } catch (ActionFailed | AEADException | NegotiatorException | IOException e) {
                            output.info("error: %s", e.getMessage());
                            output.info("skip %s", action.name());
                            buffer.reset(); // restore out
                        }
                        break;
                    case run:
                        try {
                            action = actionFactory.create();
                            output.info("run: %s", action.name());
                            init(action).run();
                            combineData(action);
                        } catch (ActionFailed | AEADException | NegotiatorException | IOException e) {
                            status = Status.unexpected_error;
                            return reportError(e);
                        }

                        break;
                    default:
                        throw new IllegalStateException(
                                String.format("unknown action type: %s", holder.type));
                }

                if (stopIfAlert && context.hasAlert()) {
                    output.info("stop, alert occurred: %s", context.getAlert());
                    break;
                }
            }
        } catch (IOException e) {
            throw new EngineException(e);
        } finally {
            output.flush();
        }

        if (status == Status.running) {
            status = Status.success;
        }

        return this;
    }

    public Engine run(Check check) {
        check.set(this);
        check.set(context);

        check.run();
        if (check.failed()) {
            throw new RuntimeException(String.format("%s check failed", check.name()));
        }
        output.info(String.format("check passed: %s", check.name()));

        return this;
    }

    public Engine apply(Analyzer analyzer) {
        analyzer.add(label, context);
        analyzer.add(label, output);
        return this;
    }

    public Status status() {
        return status;
    }

    private Engine reportError(Throwable e) throws EngineException {
        if (strict) {
            throw new EngineException("unexpected exception", e);
        }

        output.achtung("error: %s", e.getMessage());
        return this;
    }

    private void combineData(Action action) {
        ByteBuffer out = action.out();
        if (out != null && out.remaining() > 0) {
            ByteBuffer combined = ByteBuffer.allocate(buffer.remaining() + out.remaining());
            combined.put(action.out());
            combined.put(buffer);
            buffer = combined;
            buffer.position(0);
        }

        ByteBuffer data = action.applicationData();
        if (data != null && data.remaining() > 0) {
            applicationData = data;
        }
    }

    private void read(Connection connection, Action action) throws IOException {
        while (buffer.remaining() == 0 && !context.hasAlert()) {
            buffer = ByteBuffer.wrap(connection.read());
            if (buffer.remaining() == 0) {
                throw new IOException("no data received");
            }
        }

        checkCCS();

        action.in(buffer);
    }

    private void checkCCS() {
        if (checkForCCS) {
            buffer.mark();
            try {
                IncomingChangeCipherSpec incomingCCS = new IncomingChangeCipherSpec();
                output.info("met for %s", incomingCCS.name());
                init(incomingCCS);
                incomingCCS.run();
                output.info("found %s", incomingCCS.name());
            } catch (Exception e) {
                buffer.reset(); // restore out
            }
        }
    }

    public static Engine init() throws NoSuchAlgorithmException, NegotiatorException {
        Engine engine = new Engine();
        engine.context.negotiator = ECDHENegotiator.create(
                (NamedGroup.Secp) engine.context.group, engine.context.factory);
        engine.context.hkdf = HKDF.create(
                engine.context.suite.hash(), engine.context.factory);

        return engine;
    }

    private Action init(Action action) {
        action.set(output);
        action.set(context);
        action.in(buffer);
        action.applicationData(applicationData);

        return action;
    }

    public interface Condition {
        boolean met(Context context);
    }

    public interface ActionFactory {
        Action create();
    }

    private static class ActionHolder {

        private ActionType type;
        private ActionFactory factory;
        private Condition condition;

        ActionHolder(ActionFactory factory, ActionType type) {
            this.factory = factory;
            this.type = type;
        }

        private ActionHolder set(Condition condition) {
            this.condition = condition;
            return this;
        }
    }

}
