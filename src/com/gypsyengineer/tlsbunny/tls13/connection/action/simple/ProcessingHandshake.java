package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ProcessingHandshake extends AbstractAction {

    public static final ContextUpdater NOT_SPECIFIED = null;
    public static final HandshakeType NO_TYPE_SPECIFIED = null;

    private HandshakeType expectedType = NO_TYPE_SPECIFIED;
    private ContextUpdater contextUpdater;

    public ProcessingHandshake expect(HandshakeType type) {
        expectedType = type;
        return this;
    }

    @Override
    public String name() {
        return "processing a Handshake message";
    }

    public ProcessingHandshake run(ContextUpdater contextUpdater) {
        this.contextUpdater = contextUpdater;
        return this;
    }

    @Override
    public Action run() throws Exception {
        Handshake handshake = context.factory.parser().parseHandshake(in);

        HandshakeType type = handshake.getMessageType();
        if (expectedType != NO_TYPE_SPECIFIED && !expectedType.equals(type)) {
            throw new IOException(
                    String.format("expected %s, but found %s", expectedType, type));
        }

        if (contextUpdater != NOT_SPECIFIED) {
            contextUpdater.run(context, handshake);
        }

        out = ByteBuffer.wrap(handshake.getBody());
        output.info("received a Handshake message");

        return this;
    }

    public interface ContextUpdater {
        void run(Context context, Handshake handshake);
    }

}
