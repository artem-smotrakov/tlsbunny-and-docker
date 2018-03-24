package com.gypsyengineer.tlsbunny.tls13.connection;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.gypsyengineer.tlsbunny.utils.Utils.achtung;

public abstract class AbstractReceivingAction extends AbstractAction {

    @Override
    public final Action run() {
        try {
            if (buffer == null || buffer.remaining() == 0) {
                buffer = ByteBuffer.wrap(connection.read());
                if (buffer.remaining() == 0) {
                    throw new IOException("no data received");
                }
            }

            succeeded = runImpl(buffer);
        } catch (Exception e) {
            achtung("unexpected exception", e);
            succeeded = false;
        }

        return this;
    }

    abstract boolean runImpl(ByteBuffer buffer) throws Exception;

}
