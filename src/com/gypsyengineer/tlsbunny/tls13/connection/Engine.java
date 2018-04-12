package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.ECDHENegotiator;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Engine {

    private static final ByteBuffer NOTHING = ByteBuffer.allocate(0);

    private enum ActionType {
        send, require, allow, produce
    }

    public enum Status {
        not_started, running, could_not_send, unexpected_message, unexpected_error, success
    }

    private final List<ActionHolder> actions = new ArrayList<>();

    private Output output = new Output();
    private String host = "localhost";
    private int port = 443;
    private StructFactory factory = StructFactory.getDefault();
    private NamedGroup group = NamedGroup.secp256r1;
    private SignatureScheme scheme = SignatureScheme.ecdsa_secp256r1_sha256;
    private CipherSuite suite = CipherSuite.TLS_AES_128_GCM_SHA256;
    private Negotiator negotiator;
    private HKDF hkdf;
    private Status status = Status.not_started;
    private ByteBuffer buffer = NOTHING;
    private Context context = new Context();

    private List<Action> alwaysExpectedActions = new ArrayList<>();

    // if true, then stop if an alert occurred
    private boolean stopIfAlert = true;

    // this is a label to mark a particular connection
    private String label = String.valueOf(System.currentTimeMillis());

    private Engine() {

    }

    public Engine target(String host) {
        this.host = host;
        return this;
    }

    public Engine target(int port) {
        this.port = port;
        return this;
    }

    public Engine tolerant() {
        stopIfAlert = false;
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
        this.factory = factory;
        return this;
    }

    public Engine set(SignatureScheme scheme) {
        this.scheme = scheme;
        return this;
    }

    public Engine set(NamedGroup group) {
        this.group = group;
        return this;
    }

    public Engine set(Negotiator negotiator) {
        this.negotiator = negotiator;
        return this;
    }

    public Engine expect(Action action) {
        alwaysExpectedActions.add(action);
        return this;
    }

    public Engine send(Action action) {
        actions.add(new ActionHolder(action, ActionType.send));
        return this;
    }

    public Engine require(Action action) {
        actions.add(new ActionHolder(action, ActionType.require));
        return this;
    }

    public Engine allow(Action action) {
        actions.add(new ActionHolder(action, ActionType.allow));
        return this;
    }

    public Engine produce(Action action) {
        actions.add(new ActionHolder(action, ActionType.produce));
        return this;
    }

    public Engine connect() throws IOException {
        status = Status.running;
        try (Connection connection = Connection.create(host, port)) {
            buffer = NOTHING;
            context = new Context();
            context.factory = factory;

            loop: for (ActionHolder holder : actions) {
                Action action = holder.action;
                init(action);

                switch (holder.type) {
                    case send:
                        try {
                            output.info("send: %s", action.name());
                            action.run();
                            connection.send(action.data());
                        } catch (Exception e) {
                            output.info("error: %s", e.getMessage());
                            status = Status.could_not_send;
                            return this;
                        }
                        break;
                    case require:
                        output.info("require: %s", action.name());
                        read(connection, action);

                        try {
                            action.run();
                        } catch (Exception e) {
                            output.info("error: %s", e);
                            status = Status.unexpected_message;
                            return this;
                        }
                        break;
                    case allow:
                        output.info("allow: %s", action.name());
                        read(connection, action);

                        buffer.mark();
                        try {
                            action.run();
                        } catch (Exception e) {
                            output.info("error: %s", e);
                            output.info("skip %s", action.name());
                            buffer.reset(); // restore data
                        }
                        break;
                    case produce:
                        output.info("produce: %s", action.name());
                        try {
                            action.run();
                            output.info("done with producing");
                        } catch (Exception e) {
                            output.info("error: %s", e.getMessage());
                            status = Status.unexpected_error;
                            return this;
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

    private void read(Connection connection, Action action) throws IOException {
        while (buffer.remaining() == 0 && !context.hasAlert()) {
            buffer = ByteBuffer.wrap(connection.read());
            if (buffer.remaining() == 0) {
                throw new IOException("no data received");
            }

            for (Action alwaysExpectedAction : alwaysExpectedActions) {
                buffer.mark();
                try {
                    output.info("check for %s", alwaysExpectedAction.name());
                    init(alwaysExpectedAction);
                    alwaysExpectedAction.run();
                    output.info("found %s", alwaysExpectedAction.name());
                } catch (Exception e) {
                    buffer.reset(); // restore data
                }
            }
        }

        action.set(buffer);
    }

    public static Engine init() throws IOException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException {

        Engine connection = new Engine();
        connection.negotiator = ECDHENegotiator.create(
                (NamedGroup.Secp) connection.group, connection.factory);
        connection.hkdf = HKDF.create(
                connection.suite.hash(), connection.factory);

        return connection;
    }

    private void init(Action action) {
        action.set(output);
        action.set(context);
        action.set(group);
        action.set(scheme);
        action.set(suite);
        action.set(negotiator);
        action.set(factory);
        action.set(hkdf);
        action.set(buffer);
    }

    private static class ActionHolder {

        ActionHolder(Action action, ActionType type) {
            this.action = action;
            this.type = type;
        }

        Action action;
        ActionType type;
    }

}
