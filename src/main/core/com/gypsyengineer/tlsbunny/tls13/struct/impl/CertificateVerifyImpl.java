package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateVerify;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.util.Objects;

public class CertificateVerifyImpl implements CertificateVerify {

    private final SignatureScheme algorithm;
    private final Vector<Byte> signature;

    CertificateVerifyImpl(SignatureScheme algorithm, Vector<Byte> signature) {
        this.algorithm = algorithm;
        this.signature = signature;
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(algorithm, signature);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(algorithm, signature);
    }

    @Override
    public CertificateVerifyImpl copy() {
        return new CertificateVerifyImpl(
                (SignatureScheme) algorithm.copy(),
                (Vector<Byte>) signature.copy());
    }

    @Override
    public SignatureScheme getAlgorithm() {
        return algorithm;
    }

    @Override
    public Vector<Byte> getSignature() {
        return signature;
    }

    @Override
    public HandshakeType type() {
        return HandshakeTypeImpl.certificate_verify;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CertificateVerifyImpl that = (CertificateVerifyImpl) o;
        return Objects.equals(algorithm, that.algorithm) &&
                Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(algorithm, signature);
    }
}
