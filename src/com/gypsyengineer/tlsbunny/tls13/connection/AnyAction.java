package com.gypsyengineer.tlsbunny.tls13.connection;

import java.util.ArrayList;
import java.util.List;

public class AnyAction extends AbstractAction {

    private List<Action> actions = new ArrayList<>();

    public AnyAction(Action... actions) {
        for (Action action : actions) {
            this.actions.add(action);
        }
    }

    @Override
    public Action run() {
        return this;
    }

    void add(Action action) {
        actions.add(action);
    }
}
