package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import java.io.IOException;
import java.nio.ByteBuffer;

public class IncomingChangeCipherSpec extends AbstractAction {

    @Override
    public Action run() {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(connection.read());
            if (buffer.remaining() == 0) {
                throw new IOException("no data received");
            }

            TLSPlaintext tlsPlaintext = factory.parser().parseTLSPlaintext(buffer);
            if (!tlsPlaintext.containsChangeCipherSpec()) {
                throw new IOException("expected a change cipher spec message");
            }

            // TODO: read ChangeCipherSpec message (one byte)

            succeeded = true;
        } catch (IOException e) {
            succeeded = false;
        }

        return this;
    }

}
