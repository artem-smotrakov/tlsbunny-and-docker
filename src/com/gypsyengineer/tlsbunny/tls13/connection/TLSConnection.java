package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.analysis.Analyzer;

import java.util.ArrayList;
import java.util.List;

public class TLSConnection {

    private enum ActionType { SEND, EXPECT, ALLOW }

    private String host = "localhost";
    private int port = 443;
    private final List<ActionHolder> actions = new ArrayList<>();

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

    public TLSConnection run() {
        for (ActionHolder holder : actions) {

        }

        return this;
    }

    public TLSConnection analyze(Analyzer analyzer) {
        return this;
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
