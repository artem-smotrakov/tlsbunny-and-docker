package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Vector;

public interface Certificate extends HandshakeMessage {

    int CERTIFICATE_LIST_LENGTH_BYTES = 3;
    int CONTEXT_LENGTH_BYTES = 1;

    Vector<CertificateEntry> getCertificateList();
    Vector<Byte> getCertificateRequestContext();
}
