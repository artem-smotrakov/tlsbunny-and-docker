package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import java.io.IOException;

public class IncomingEncryptedExtensions extends AbstractReceivingAction {

    @Override
    public String name() {
        return "receiving EncryptedExtensions";
    }

    @Override
    void runImpl() throws Exception {
        TLSPlaintext tlsPlaintext = factory.parser().parseTLSPlaintext(buffer);
        if (!tlsPlaintext.containsApplicationData()) {
            throw new IOException("expected a TLSCiphertext");
        }

        TLSInnerPlaintext tlsInnerPlaintext = factory.parser().parseTLSInnerPlaintext(
                context.handshakeDecryptor.decrypt(tlsPlaintext));

        if (!tlsInnerPlaintext.containsHandshake()) {
            throw new IOException("expected a handshake message");
        }

        Handshake handshake = factory.parser().parseHandshake(
                tlsInnerPlaintext.getContent());

        if (!handshake.containsEncryptedExtensions()) {
            throw new IOException("expected a EncryptedExtensions message");
        }

        processEncryptedExtensions(handshake);
    }

    private void processEncryptedExtensions(Handshake handshake) {
        factory.parser().parseEncryptedExtensions(handshake.getBody());
        context.setEncryptedExtensions(handshake);
    }
}
