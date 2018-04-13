package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.nio.ByteBuffer;

public class WrappingIntoHandshake extends AbstractAction {

    public static final ContextUpdater NOT_SPECIFIED = null;

    private final HandshakeType type;
    private final ContextUpdater contextUpdater;

    public WrappingIntoHandshake(HandshakeType type, ContextUpdater contextUpdater) {
        this.type = type;
        this.contextUpdater = contextUpdater;
    }

    public WrappingIntoHandshake(HandshakeType type) {
        this(type, NOT_SPECIFIED);
    }

    @Override
    public String name() {
        return String.format("wrapping into Handshake (%s)", type);
    }

    @Override
    public Action run() throws Exception {
        Handshake handshake = factory.createHandshake(type, in.array());

        if (contextUpdater != NOT_SPECIFIED) {
            contextUpdater.run(context, handshake);
        }

        out = ByteBuffer.wrap(handshake.encoding());

        return this;
    }

    public interface ContextUpdater {
        void run(Context context, Handshake handshake);
    }
}
