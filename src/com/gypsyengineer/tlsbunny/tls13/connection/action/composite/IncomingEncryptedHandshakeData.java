package com.gypsyengineer.tlsbunny.tls13.connection.action.composite;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

import java.io.IOException;
import java.nio.ByteBuffer;

public class IncomingEncryptedHandshakeData extends AbstractAction {

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

        out = ByteBuffer.wrap(tlsInnerPlaintext.getContent());

        output.info("received encrypted handshake data (%d bytes)",
                tlsInnerPlaintext.getContent().length);

        return this;
    }

    
}
