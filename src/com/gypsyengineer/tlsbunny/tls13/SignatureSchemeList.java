package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SignatureSchemeList implements Entity {

    public static final int LENGTH_BYTES = 2;
    public static final int MIN_EXPECTED_LENGTH = 2;
    public static final int MAX_EXPECTED_LENGTH = 65534;

    private Vector<SignatureScheme> supported_signature_algorithms;

    public SignatureSchemeList(Vector<SignatureScheme> supported_signature_algorithms) {
        this.supported_signature_algorithms = supported_signature_algorithms;
    }

    public void set(SignatureScheme signatureScheme) {
        supported_signature_algorithms = Vector.wrap(LENGTH_BYTES, signatureScheme);
    }

    public Vector<SignatureScheme> getSupportedSignatureAlgorithms() {
        return supported_signature_algorithms;
    }

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

    public static SignatureSchemeList create(SignatureScheme... signatureSchemes) {
        return new SignatureSchemeList(Vector.wrap(LENGTH_BYTES, signatureSchemes));
    }

    public static SignatureSchemeList parse(ByteBuffer buffer) {
        return new SignatureSchemeList(
                Vector.parse(buffer, LENGTH_BYTES, buf -> SignatureScheme.parse(buf)));
    }

}
