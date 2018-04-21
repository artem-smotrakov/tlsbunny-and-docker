package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ProcessingHandshakeTLSCiphertext extends AbstractAction {

    public static final ContentType NO_TYPE_SPECIFIED = null;

    private ContentType expectedType = NO_TYPE_SPECIFIED;

    public ProcessingHandshakeTLSCiphertext expect(ContentType type) {
        expectedType = type;
        return this;
    }


    @Override
    public String name() {
        return "processing TLSCiphertext with handshake data";
    }

    @Override
    public Action run() throws Exception {
        TLSPlaintext tlsPlaintext = context.factory.parser().parseTLSPlaintext(in);

        if (!tlsPlaintext.containsApplicationData()) {
            throw new IOException("expected a TLSCiphertext");
        }

        byte[] plaintext = context.handshakeDecryptor.decrypt(tlsPlaintext);
        TLSInnerPlaintext tlsInnerPlaintext = context.factory.parser()
                .parseTLSInnerPlaintext(plaintext);

        ContentType type = tlsInnerPlaintext.getType();
        if (expectedType != NO_TYPE_SPECIFIED && !expectedType.equals(type)) {
            throw new IOException(
                    String.format("expected %s, but found %s", expectedType, type));
        }

        out = ByteBuffer.wrap(tlsInnerPlaintext.getContent());
        output.info("decrypted a TLSCipertext");

        return this;
    }

    
}
