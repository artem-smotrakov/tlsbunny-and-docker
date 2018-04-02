package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import java.io.IOException;

public class IncomingCertificateVerify extends AbstractAction {

    @Override
    public String name() {
        return "CertificateVerify";
    }

    @Override
    public Action run() throws Exception {
        Handshake handshake = processEncryptedHandshake();
        if (!handshake.containsCertificateVerify()) {
            throw new IOException("expected a CertificateVerify message");
        }

        processCertificateVerify(handshake);

        return this;
    }

    private void processCertificateVerify(Handshake handshake) {
        factory.parser().parseCertificateVerify(handshake.getBody());
        context.setServerCertificateVerify(handshake);
    }
}
