package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.struct.Alert;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

import java.io.IOException;
import java.nio.ByteBuffer;

public class EncryptedAlert extends AbstractAction {

    enum Phase { handshake, application_data };

    private final Phase phase;

    public EncryptedAlert(Phase phase) {
        this.phase = phase;
    }

    @Override
    public String name() {
        return "encrypted alert";
    }

    @Override
    public Action run() throws Exception {
        TLSPlaintext tlsPlaintext = factory.parser().parseTLSPlaintext(in);

        if (!tlsPlaintext.containsApplicationData()) {
            throw new IOException("expected encrypted data");
        }

        AEAD decryptor;
        switch (phase) {
            case handshake:
                decryptor = context.handshakeDecryptor;
                break;
            case application_data:
                decryptor = context.applicationDataDecryptor;
                break;
            default:
                throw new IllegalArgumentException();
        }

        byte[] plaintext = decryptor.decrypt(tlsPlaintext);
        try {
            TLSInnerPlaintext tlsInnerPlaintext = factory.parser()
                    .parseTLSInnerPlaintext(plaintext);

            if (!tlsInnerPlaintext.containsAlert()) {
                throw new IOException("expected an alert");
            }

            Alert alert = factory.parser().parseAlert(tlsInnerPlaintext.getContent());
            context.setAlert(alert);
            output.info("received an alert: %s", alert);
        } catch (Exception e) {
            out = ByteBuffer.wrap(plaintext);
            throw e;
        }

        return this;
    }

    
}
