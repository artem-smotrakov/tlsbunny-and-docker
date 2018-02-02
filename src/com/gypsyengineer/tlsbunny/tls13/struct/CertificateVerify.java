package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Vector;

public interface CertificateVerify extends HandshakeMessage {

    int SIGNATURE_LENGTH_BYTES = 2;

    SignatureScheme getAlgorithm();
    Vector<Byte> getSignature();
}
