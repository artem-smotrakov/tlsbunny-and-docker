package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

import java.io.IOException;
import java.nio.ByteBuffer;

public class IncomingEncryptedHandshake extends AbstractAction {

    @Override
    public String name() {
        return "encrypted handshake data";
    }

    @Override
    public Action run() throws Exception {
        TLSPlaintext tlsPlaintext = factory.parser().parseTLSPlaintext(in);

        if (!tlsPlaintext.containsApplicationData()) {
            throw new IOException("expected encrypted data");
        }

        TLSInnerPlaintext tlsInnerPlaintext = factory.parser().parseTLSInnerPlaintext(
                context.handshakeDecryptor.decrypt(tlsPlaintext));

        if (!tlsInnerPlaintext.containsHandshake()) {
            throw new IOException("expected encrypted handshake data");
        }

        out = ByteBuffer.wrap(tlsInnerPlaintext.getContent());

        output.info("received encrypted handshake data (%d bytes)",
                tlsInnerPlaintext.getContent().length);

        return this;
    }

    
}
