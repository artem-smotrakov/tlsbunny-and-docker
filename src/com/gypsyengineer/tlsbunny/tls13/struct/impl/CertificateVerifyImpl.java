package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeImpl;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateVerify;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CertificateVerifyImpl implements CertificateVerify {


    private SignatureSchemeImpl algorithm;
    private Vector<Byte> signature;

    CertificateVerifyImpl(SignatureSchemeImpl algorithm, Vector<Byte> signature) {
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
    public SignatureSchemeImpl getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(SignatureSchemeImpl algorithm) {
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
    public HandshakeTypeImpl type() {
        return HandshakeTypeImpl.certificate_verify;
    }

    public static CertificateVerifyImpl parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }
    
    public static CertificateVerifyImpl parse(ByteBuffer buffer) {
        return new CertificateVerifyImpl(
                SignatureSchemeImpl.parse(buffer), 
                Vector.parseOpaqueVector(buffer, SIGNATURE_LENGTH_BYTES));
    }

}
