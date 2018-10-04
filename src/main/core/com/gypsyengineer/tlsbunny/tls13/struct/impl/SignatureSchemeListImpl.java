package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureSchemeList;
import java.io.IOException;
import java.util.Objects;

public class SignatureSchemeListImpl implements SignatureSchemeList {

    private Vector<SignatureScheme> supported_signature_algorithms;

    SignatureSchemeListImpl(Vector<SignatureScheme> supported_signature_algorithms) {
        this.supported_signature_algorithms = supported_signature_algorithms;
    }

    @Override
    public Vector<SignatureScheme> getSupportedSignatureAlgorithms() {
        return supported_signature_algorithms;
    }

    @Override
    public int encodingLength() {
        return supported_signature_algorithms.encodingLength();
    }

    @Override
    public byte[] encoding() throws IOException {
        return supported_signature_algorithms.encoding();
    }

    @Override
    public SignatureSchemeListImpl copy() {
        return new SignatureSchemeListImpl(
                (Vector<SignatureScheme>) supported_signature_algorithms.copy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SignatureSchemeListImpl that = (SignatureSchemeListImpl) o;
        return Objects.equals(supported_signature_algorithms, that.supported_signature_algorithms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supported_signature_algorithms);
    }
}
