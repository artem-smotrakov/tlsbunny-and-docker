package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.ECDHENegotiator;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Connection;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class TLSConnection {

    private static final byte[] NOTHING = new byte[0];

    private enum ActionType { SEND, EXPECT, ALLOW }

    public enum Status { NOT_STARTED, RUNNING, COULD_NOT_SEND, UNEXPECTED_MESSAGE, SUCCESS }

    private final List<ActionHolder> actions = new ArrayList<>();
    private String host = "localhost";
    private int port = 443;
    private StructFactory factory = StructFactory.getDefault();
    private NamedGroup group = NamedGroup.secp256r1;
    private SignatureScheme scheme = SignatureScheme.ecdsa_secp256r1_sha256;
    private Negotiator negotiator;
    private CipherSuite suite = CipherSuite.TLS_AES_128_GCM_SHA256;
    private Status status = Status.NOT_STARTED;

    private TLSConnection() {

    }

    public TLSConnection host(String host) {
        this.host = host;
        return this;
    }

    public TLSConnection port(int port) {
        this.port = port;
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
        actions.add(new ActionHolder(action, ActionType.SEND));
        return this;
    }

    public TLSConnection expect(Action action) {
        actions.add(new ActionHolder(action, ActionType.EXPECT));
        return this;
    }

    public TLSConnection allow(Action action) {
        actions.add(new ActionHolder(action, ActionType.ALLOW));
        return this;
    }

    public TLSConnection run() throws IOException {
        status = Status.RUNNING;
        try (Connection connection = Connection.create(host, port)) {
            Context context = new Context();
            byte[] unprocessed = NOTHING;
            loop: for (ActionHolder holder : actions) {
                Action action = holder.action;

                action.set(context);
                action.set(group);
                action.set(scheme);
                action.set(negotiator);
                action.set(factory);
                action.set(connection);
                if (unprocessed.length != 0) {
                    action.set(unprocessed);
                    unprocessed = NOTHING;
                }

                action.run();

                switch (holder.type) {
                    case SEND:
                        if (!action.succeeded()) {
                            status = Status.COULD_NOT_SEND;
                            break loop;
                        }
                        break;
                    case EXPECT:
                        if (!action.succeeded()) {
                            status = Status.UNEXPECTED_MESSAGE;
                            break loop;
                        }
                        break;
                    case ALLOW:
                        if (!action.succeeded()) {
                            unprocessed = action.data();
                        }
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
        }

        if (status == Status.RUNNING) {
            status = Status.SUCCESS;
        }

        return this;
    }

    public TLSConnection check(Check check) {
        check.run();
        if (check.failed()) {
            throw new RuntimeException(String.format("check failed: %s", check.name()));
        }

        return this;
    }

    public TLSConnection analyze(Analyzer analyzer) {
        return this;
    }

    public Status status() {
        return status;
    }

    public static TLSConnection create()
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {

        TLSConnection connection = new TLSConnection();
        connection.negotiator = ECDHENegotiator.create(
                (NamedGroup.Secp) connection.group, connection.factory);

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
