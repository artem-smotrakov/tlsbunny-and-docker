package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateVerify;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;

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

}
