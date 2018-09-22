package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.connection.action.ActionFailed;
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
        run,
        send, send_while,
        receive, receive_while,
        store, restore
    }

    public enum Status {
        not_started, running, could_not_send, unexpected_error, success
    }

    private final List<ActionHolder> actions = new ArrayList<>();

    private Connection connection;
    private Output output = new Output();
    private String host = "localhost";
    private int port = 443;
    private Status status = Status.not_started;
    private ByteBuffer buffer = NOTHING;
    private ByteBuffer applicationData = NOTHING;
    private Context context = new Context();

    private Throwable exception;

    // timeout for reading incoming data (in millis)
    private long timeout = Connection.default_read_timeout;

    // if true, then stop if an alert occurred
    private boolean stopIfAlert = true;

    // if true, then an exception is thrown in case of error
    private boolean strict = false;

    // this is a label to mark a particular connection
    private String label = String.format("connection:%d", System.currentTimeMillis());

    private final List<byte[]> storedData = new ArrayList<>();

    private Engine() {
        context.group = NamedGroup.secp256r1;
        context.scheme = SignatureScheme.ecdsa_secp256r1_sha256;
        context.suite = CipherSuite.TLS_AES_128_GCM_SHA256;
        context.factory = StructFactory.getDefault();
    }

    public Context context() {
        return context;
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

    public Throwable exception() {
        return exception;
    }

    public Engine nice() {
        strict = false;
        return this;
    }

    public Engine strict() {
        strict = true;
        return this;
    }

    public Engine continueIfAlert() {
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

    public Engine set(Connection connection) {
        this.connection = connection;
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

    public Engine store() {
        actions.add(new ActionHolder()
                .engine(this)
                .factory(() -> new EmptyAction())
                .type(ActionType.store));
        return this;
    }

    public Engine restore() {
        actions.add(new ActionHolder()
                .engine(this)
                .factory(() -> new EmptyAction())
                .type(ActionType.restore));
        return this;
    }

    public Engine send(Action action) {
        actions.add(new ActionHolder()
                .engine(this)
                .factory(() -> action)
                .type(ActionType.send));
        return this;
    }

    public Engine send(int n, ActionFactory factory) {
        for (int i=0; i<n; i++) {
            send(factory.create());
        }
        return this;
    }

    public Engine receive(Action action) {
        actions.add(new ActionHolder()
                .engine(this)
                .factory(() -> action)
                .type(ActionType.receive));
        return this;
    }

    public ActionHolder receive(ActionFactory factory) {
        return new ActionHolder().engine(this).factory(factory);
    }

    public ActionHolder loop(Condition condition) {
        return new ActionHolder()
                .engine(this)
                .type(ActionType.receive_while)
                .condition(condition);
    }

    public Engine run(Action action) {
        actions.add(new ActionHolder()
                .engine(this)
                .factory(() -> action)
                .type(ActionType.run));
        return this;
    }

    public Engine connect() throws EngineException {
        context.negotiator.set(output);
        status = Status.running;

        try (Connection connection = initConnection()) {
            buffer = NOTHING;

            loop: for (ActionHolder holder : actions) {
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
                    case receive_while:
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
                    case store:
                        storeImpl();
                        break;
                    case restore:
                        restoreImpl();
                        break;
                    default:
                        throw new IllegalStateException(
                                String.format("unknown action type: %s", holder.type));
                }

                if (stopIfAlert && context.hasAlert()) {
                    output.info("stop, alert occurred: %s", context.getAlert());
                    break;
                }

                output.flush();
            }
        } catch (IOException e) {
            reportError(e);
        } finally {
            output.flush();
        }

        if (status == Status.running) {
            status = Status.success;
        }

        return this;
    }

    public Engine run(List<Check> checks) throws ActionFailed {
        for (Check check : checks) {
            check.set(this);
            check.set(context);

            check.run();
            if (check.failed()) {
                throw new ActionFailed(String.format("%s check failed", check.name()));
            }
            output.info(String.format("check passed: %s", check.name()));
        }

        return this;
    }

    public Engine run(Check... checks) throws ActionFailed {
        return run(List.of(checks));
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
        exception = e;
        if (strict) {
            throw new EngineException("unexpected exception", e);
        }

        output.achtung("error: %s", e.toString());
        return this;
    }

    private void storeImpl() {
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        storedData.add(data);
        buffer = NOTHING;
        output.info("stored %d bytes", data.length);
    }

    private void restoreImpl() {
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        int n = data.length;
        for (byte[] bytes : storedData) {
            n += bytes.length;
        }

        buffer = ByteBuffer.allocate(n);

        for (byte[] bytes : storedData) {
            buffer.put(bytes);
        }

        buffer.put(data);
        buffer.flip();

        output.info("restored %d bytes", n);
    }

    private void combineData(Action action) {
        ByteBuffer out = action.out();
        if (out != null && out.remaining() > 0) {
            ByteBuffer combined = ByteBuffer.allocate(buffer.remaining() + out.remaining());
            combined.put(out);
            combined.put(buffer);
            buffer = combined;
            buffer.flip();
        }

        ByteBuffer data = action.applicationData();
        if (data != null && data.remaining() > 0) {
            applicationData = data;
        }
    }

    private void read(Connection connection, Action action) throws IOException {
        if (connection.isClosed()) {
            throw new IOException("connection was closed");
        }

        while (buffer.remaining() == 0) {
            buffer = ByteBuffer.wrap(connection.read());
            if (buffer.remaining() == 0) {
                throw new IOException("no data received");
            }
        }

        action.in(buffer);
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

    private Connection initConnection() throws IOException {
        if (connection != null) {
            return connection;
        }

        if (host != null && port > 0) {
            return Connection.create(host, port, timeout);
        }

        throw new IllegalArgumentException(
                "what the hell? connection can't be initialized!");
    }

    public interface Condition {
        boolean met(Context context);
    }

    public interface ActionFactory {
        Action create();
    }

    public static class ActionHolder {

        private Engine engine;
        private ActionType type;
        private ActionFactory factory;
        private Condition condition;

        public Engine receive(ActionFactory factory) {
            factory(factory);
            engine.actions.add(this);
            return engine;
        }

        private ActionHolder engine(Engine engine) {
            this.engine = engine;
            return this;
        }

        private ActionHolder factory(ActionFactory factory) {
            this.factory = factory;
            return this;
        }

        private ActionHolder type(ActionType type) {
            this.type = type;
            return this;
        }

        private ActionHolder condition(Condition condition) {
            this.condition = condition;
            return this;
        }
    }

    // this is an action that does nothing
    private static class EmptyAction implements Action {

        @Override
        public String name() {
            return "I am a fake action, you're probably not supposed to call this method!";
        }

        @Override
        public Action set(Output output) {
            return this;
        }

        @Override
        public Action set(Context context) {
            return this;
        }

        @Override
        public Action run() {
            return this;
        }

        @Override
        public Action in(byte[] bytes) {
            return this;
        }

        @Override
        public Action in(ByteBuffer buffer) {
            throw new UnsupportedOperationException("what the hell? I can't do that!");
        }

        @Override
        public ByteBuffer out() {
            throw new UnsupportedOperationException("what the hell? I can't do that!");
        }

        @Override
        public Action applicationData(ByteBuffer buffer) {
            return this;
        }

        @Override
        public ByteBuffer applicationData() {
            throw new UnsupportedOperationException("what the hell? I can't do that!");
        }
    }

}
