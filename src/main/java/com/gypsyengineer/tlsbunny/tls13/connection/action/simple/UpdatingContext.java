package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;

public class UpdatingContext extends AbstractAction {

    private final Context.Element element;

    public UpdatingContext(Context.Element item) {
        this.element = item;
    }

    @Override
    public String name() {
        return String.format("updating context (%s)", element);
    }

    @Override
    public Action run() throws Exception {
        in.mark();
        try {
            context.set(element, context.factory.parser().parseHandshake(in));
        } finally {
            in.reset();
        }

        return this;
    }

}
