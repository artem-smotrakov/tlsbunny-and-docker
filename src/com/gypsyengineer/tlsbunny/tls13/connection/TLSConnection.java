package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.ECDHENegotiator;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class TLSConnection {

    private static final ByteBuffer NOTHING = ByteBuffer.allocate(0);

    private enum ActionType {
        send, expect, allow
    }

    public enum Status {
        not_started, running, could_not_send, unexpected_message, success
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

    private TLSConnection() {

    }

    public TLSConnection target(String host) {
        this.host = host;
        return this;
    }

    public TLSConnection target(int port) {
        this.port = port;
        return this;
    }

    public TLSConnection set(Output output) {
        this.output = output;
        return this;
    }

    public TLSConnection set(StructFactory factory) {
        this.factory = factory;
        return this;
    }

    public TLSConnection set(SignatureScheme scheme) {
        this.scheme = scheme;
        return this;
    }

    public TLSConnection set(NamedGroup group) {
        this.group = group;
        return this;
    }

    public TLSConnection set(Negotiator negotiator) {
        this.negotiator = negotiator;
        return this;
    }

    public TLSConnection send(Action action) {
        actions.add(new ActionHolder(action, ActionType.send));
        return this;
    }

    public TLSConnection expect(Action action) {
        actions.add(new ActionHolder(action, ActionType.expect));
        return this;
    }

    public TLSConnection allow(Action action) {
        actions.add(new ActionHolder(action, ActionType.allow));
        return this;
    }

    public TLSConnection connect() throws IOException {
        status = Status.running;
        try (Connection connection = Connection.create(host, port)) {
            Context context = new Context();
            context.factory = factory;

            ByteBuffer buffer = NOTHING;
            loop: for (ActionHolder holder : actions) {
                Action action = holder.action;

                action.set(output);
                action.set(context);
                action.set(group);
                action.set(scheme);
                action.set(suite);
                action.set(negotiator);
                action.set(factory);
                action.set(hkdf);
                action.set(connection);
                action.set(buffer);

                switch (holder.type) {
                    case send:
                        try {
                            output.info(action.name());
                            action.run();
                            connection.send(action.data());
                            output.info("done with %s", action.name());
                        } catch (Exception e) {
                            output.info("could not send: %s", e.getMessage());
                            status = Status.could_not_send;
                            return this;
                        }
                        break;
                    case expect:
                        try {
                            output.info("expect %s", action.name());
                            if (buffer.remaining() == 0) {
                                buffer = ByteBuffer.wrap(connection.read());
                                if (buffer.remaining() == 0) {
                                    throw new IOException("no data received");
                                }
                                action.set(buffer);
                            }

                            action.run();
                            output.info("done with %s", action.name());
                        } catch (Exception e) {
                            output.info("could not receive: %s", e.getMessage());
                            status = Status.unexpected_message;
                            return this;
                        }
                        break;
                    case allow:
                        // TODO: duplicate code, see above
                        if (buffer.remaining() == 0) {
                            buffer = ByteBuffer.wrap(connection.read());
                            if (buffer.remaining() == 0) {
                                throw new IOException("no data received");
                            }
                            action.set(buffer);
                        }

                        int index = buffer.position();
                        try {
                            output.info("try %s", action.name());
                            action.run();
                            output.info("done with %s", action.name());
                        } catch (Exception e) {
                            output.info("failed: %s", e.getMessage());
                            output.info("skip %s", action.name());

                            // restore data
                            buffer.position(index);
                        }
                        break;
                    default:
                        throw new IllegalStateException(
                                String.format("unknown action type: %s", holder.type));
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

    public TLSConnection check(Check check) {
        check.set(this);
        check.run();
        if (check.failed()) {
            throw new RuntimeException(String.format("%s check failed", check.name()));
        }

        return this;
    }

    public TLSConnection analyze(Analyzer analyzer) {
        return this;
    }

    public Status status() {
        return status;
    }

    public static TLSConnection init() throws IOException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException {

        TLSConnection connection = new TLSConnection();
        connection.negotiator = ECDHENegotiator.create(
                (NamedGroup.Secp) connection.group, connection.factory);
        connection.hkdf = HKDF.create(
                connection.suite.hash(), connection.factory);

        return connection;
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
