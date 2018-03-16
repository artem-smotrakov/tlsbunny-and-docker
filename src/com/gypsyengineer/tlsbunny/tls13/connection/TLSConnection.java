package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.utils.Connection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TLSConnection {

    private static final byte[] NOTHING = new byte[0];

    private enum ActionType { SEND, EXPECT, ALLOW }

    public enum Status { NOT_STARTED, COULD_NOT_SEND, UNEXPECTED_MESSAGE, SUCCESS }

    private final List<ActionHolder> actions = new ArrayList<>();
    private String host = "localhost";
    private int port = 443;
    private Status status = Status.NOT_STARTED;

    public TLSConnection() {

    }

    public TLSConnection host(String host) {
        this.host = host;
        return this;
    }

    public TLSConnection port(int port) {
        this.port = port;
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
        try (Connection connection = Connection.create(host, port)) {
            byte[] unprocessed = NOTHING;
            loop: for (ActionHolder holder : actions) {
                holder.action.set(connection);
                if (unprocessed.length != 0) {
                    holder.action.set(unprocessed);
                    unprocessed = NOTHING;
                }

                holder.action.run();

                switch (holder.type) {
                    case SEND:
                        if (!holder.action.succeeded()) {
                            status = Status.COULD_NOT_SEND;
                            break loop;
                        }
                        break;
                    case EXPECT:
                        if (!holder.action.succeeded()) {
                            status = Status.UNEXPECTED_MESSAGE;
                            break loop;
                        }
                        break;
                    case ALLOW:
                        if (!holder.action.succeeded()) {
                            unprocessed = holder.action.data();
                        }
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
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

    private static class ActionHolder {

        ActionHolder(Action action, ActionType type) {
            this.action = action;
            this.type = type;
        }

        Action action;
        ActionType type;
    }

}
