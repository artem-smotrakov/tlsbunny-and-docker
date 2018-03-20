package com.gypsyengineer.tlsbunny.tls13.connection;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AnyAction extends AbstractReceivingAction {

    private List<Action> actions = new ArrayList<>();

    public AnyAction(Action... actions) {
        for (Action action : actions) {
            this.actions.add(action);
        }
    }

    @Override
    boolean runImpl(ByteBuffer buffer) throws Exception {
        return false;
    }

    void add(Action action) {
        actions.add(action);
    }
}
