package com.gypsyengineer.tlsbunny.tls13.connection;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class AbstractReceivingAction extends AbstractAction {

    @Override
    public final Action run() {
        ByteBuffer buffer = null;
        try {
            if (data != null && data.length > 0) {
                buffer = ByteBuffer.wrap(data);
            } else {
                buffer = ByteBuffer.wrap(connection.read());
                if (buffer.remaining() == 0) {
                    throw new IOException("no data received");
                }
            }

            succeeded = runImpl(buffer);
        } catch (Exception e) {
            succeeded = false;
        } finally {
            if (buffer != null && buffer.remaining() == 0) {
                data = new byte[buffer.remaining()];
                buffer.get(data);
            }
        }

        return this;
    }

    abstract boolean runImpl(ByteBuffer buffer) throws Exception;

}
