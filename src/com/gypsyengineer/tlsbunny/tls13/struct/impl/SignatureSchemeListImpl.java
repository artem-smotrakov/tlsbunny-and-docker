package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureSchemeList;

public class SignatureSchemeListImpl implements SignatureSchemeList {

    private Vector<SignatureScheme> supported_signature_algorithms;

    public SignatureSchemeListImpl(Vector<SignatureScheme> supported_signature_algorithms) {
        this.supported_signature_algorithms = supported_signature_algorithms;
    }

    @Override
    public void set(SignatureScheme signatureScheme) {
        supported_signature_algorithms = Vector.wrap(LENGTH_BYTES, signatureScheme);
    }

    @Override
    public Vector<SignatureScheme> getSupportedSignatureAlgorithms() {
        return supported_signature_algorithms;
    }

    @Override
    public void setSupportedSignatureAlgorithms(
            Vector<SignatureScheme> supported_signature_algorithms) {

        this.supported_signature_algorithms = supported_signature_algorithms;
    }
    
    @Override
    public int encodingLength() {
        return supported_signature_algorithms.encodingLength();
    }

    @Override
    public byte[] encoding() throws IOException {
        return supported_signature_algorithms.encoding();
    }

}
