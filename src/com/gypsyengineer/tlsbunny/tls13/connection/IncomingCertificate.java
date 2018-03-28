package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import java.io.IOException;

public class IncomingCertificate extends AbstractAction {

    @Override
    public String name() {
        return "receiving Certificate";
    }

    @Override
    public Action run() throws Exception {
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

        if (!handshake.containsCertificate()) {
            throw new IOException("expected a Certificate message");
        }

        processCertificate(handshake);

        return this;
    }

    private void processCertificate(Handshake handshake) {
        factory.parser().parseCertificate(
                handshake.getBody(),
                buf -> factory.parser().parseX509CertificateEntry(buf));
        context.setServerCertificate(handshake);
    }
}
