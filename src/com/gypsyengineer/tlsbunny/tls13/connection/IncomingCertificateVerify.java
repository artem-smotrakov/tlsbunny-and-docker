package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import java.io.IOException;
import java.nio.ByteBuffer;

public class IncomingCertificateVerify extends AbstractReceivingAction {

    @Override
    public String description() {
        return "receive CertificateVerify";
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

        if (!handshake.containsCertificateVerify()) {
            throw new IOException("expected a CertificateVerify message");
        }

        processCertificateVerify(handshake);
    }

    private void processCertificateVerify(Handshake handshake) {
        factory.parser().parseCertificateVerify(handshake.getBody());
        context.setServerCertificateVerify(handshake);
    }
}
