package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import java.io.IOException;

public class IncomingCertificate extends AbstractAction {

    @Override
    public String name() {
        return "Certificate";
    }

    @Override
    public Action run() throws Exception {
        Handshake handshake = processEncryptedHandshake();
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
