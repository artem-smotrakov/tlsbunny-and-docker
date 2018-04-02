package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.CertificateRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

import java.io.IOException;

public class IncomingCertificateRequest extends AbstractAction {

    @Override
    public String name() {
        return "CertificateRequest";
    }

    @Override
    public Action run() throws Exception {
        Handshake handshake = processEncryptedHandshake();
        if (!handshake.containsCertificateRequest()) {
            throw new IOException("expected a CertificateRequest message");
        }

        processCertificateRequest(handshake);

        return this;
    }

    private void processCertificateRequest(Handshake handshake) {
        CertificateRequest certificateRequest = factory.parser().parseCertificateRequest(
                handshake.getBody());
        context.certificate_request_context = certificateRequest.getCertificateRequestContext();
        context.setServerCertificateRequest(handshake);
    }

}
