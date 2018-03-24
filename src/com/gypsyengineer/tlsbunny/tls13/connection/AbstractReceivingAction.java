package com.gypsyengineer.tlsbunny.tls13.connection;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.gypsyengineer.tlsbunny.utils.Utils.achtung;

public abstract class AbstractReceivingAction extends AbstractAction {

    @Override
    public final Action run() throws Exception {
        if (buffer == null || buffer.remaining() == 0) {
            buffer = ByteBuffer.wrap(connection.read());
            if (buffer.remaining() == 0) {
                throw new IOException("no data received");
            }
        }

        int index = buffer.position();
        try {
            runImpl();
        } catch (Exception e) {
            // restore data before re-throwing
            buffer.position(index);
            throw e;
        }

        return this;
    }

    abstract void runImpl() throws Exception;

}
