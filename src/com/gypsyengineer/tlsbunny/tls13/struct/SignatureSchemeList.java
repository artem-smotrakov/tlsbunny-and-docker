package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;

// TODO: make it immutable
public interface SignatureSchemeList extends Struct {

    int LENGTH_BYTES = 2;

    Vector<SignatureScheme> getSupportedSignatureAlgorithms();
    void set(SignatureScheme signatureScheme);
    void setSupportedSignatureAlgorithms(Vector<SignatureScheme> supported_signature_algorithms);
}
