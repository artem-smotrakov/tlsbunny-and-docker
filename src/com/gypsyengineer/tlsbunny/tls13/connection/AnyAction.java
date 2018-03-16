package com.gypsyengineer.tlsbunny.tls13.connection;

import java.util.ArrayList;
import java.util.List;

public class AnyAction implements Action {

    private List<Action> actions = new ArrayList<>();

    public AnyAction(Action... actions) {
        for (Action action : actions) {
            this.actions.add(action);
        }
    }

    @Override
    public boolean hasData() {
        return false;
    }

    @Override
    public boolean needsData() {
        return false;
    }

    @Override
    public void send() {

    }

    @Override
    public void receive() {

    }

    void add(Action action) {
        actions.add(action);
    }
}
