package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateVerify;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CertificateVerifyImpl implements CertificateVerify {

    private SignatureScheme algorithm;
    private Vector<Byte> signature;

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
    public void setAlgorithm(SignatureScheme algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public Vector<Byte> getSignature() {
        return signature;
    }

    @Override
    public void setSignature(Vector<Byte> signature) {
        this.signature = signature;
    }

    @Override
    public HandshakeType type() {
        return HandshakeTypeImpl.certificate_verify;
    }

    public static CertificateVerify parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }
    
    public static CertificateVerify parse(ByteBuffer buffer) {
        return new CertificateVerifyImpl(
                SignatureSchemeImpl.parse(buffer), 
                Vector.parseOpaqueVector(buffer, SIGNATURE_LENGTH_BYTES));
    }

}
