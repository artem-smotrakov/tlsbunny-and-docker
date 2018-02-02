package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Vector;

public interface CertificateRequest extends HandshakeMessage {

    int CERTIFICATE_REQUEST_CONTEXT_LENGTH_BYTES = 1;
    int EXTENSIONS_LENGTH_BYTES = 2;

    Vector<Byte> getCertificateRequestContext();
    Vector<Extension> getExtensions();
}
