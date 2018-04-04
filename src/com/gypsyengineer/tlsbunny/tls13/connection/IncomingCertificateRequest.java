package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.tls13.utils.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private void processCertificateRequest(Handshake handshake) throws IOException {
        CertificateRequest certificateRequest = factory.parser().parseCertificateRequest(
                handshake.getBody());
        context.certificate_request_context = certificateRequest.getCertificateRequestContext();
        context.setServerCertificateRequest(handshake);

        Extension extension = Helper.findExtension(
                ExtensionType.signature_algorithms,
                certificateRequest.getExtensions().toList());

        if (extension == null) {
            throw new IOException("no signature_algorithms extension");
        }

        SignatureSchemeList list = factory.parser().parseSignatureSchemeList(
                extension.getExtensionData().bytes());

        List<String> signature_algorithms = new ArrayList<>();
        for (SignatureScheme scheme : list.getSupportedSignatureAlgorithms().toList()) {
            signature_algorithms.add(scheme.toString());
        }
        output.info("CertificateRequest, algorithms: %s", String.join(", ", signature_algorithms));
    }

}
