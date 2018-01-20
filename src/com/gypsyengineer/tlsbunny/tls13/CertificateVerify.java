package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CertificateVerify implements HandshakeMessage {

    public static final int SIGNATURE_LENGTH_BYTES = 2;
    public static final int MIN_EXTECTED_SIGNATURE_LENGTH = 0;
    public static final int MAX_EXTECTED_SIGNATURE_LENGTH = 65535;

    private SignatureScheme algorithm;
    private Vector<Byte> signature;

    public CertificateVerify(SignatureScheme algorithm, Vector<Byte> signature) {
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

    public SignatureScheme getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(SignatureScheme algorithm) {
        this.algorithm = algorithm;
    }

    public Vector<Byte> getSignature() {
        return signature;
    }

    public void setSignature(Vector<Byte> signature) {
        this.signature = signature;
    }

    @Override
    public HandshakeType type() {
        return HandshakeType.CERTIFICATE_VERIFY;
    }

    public static CertificateVerify parse(ByteBuffer buffer) {
        return new CertificateVerify(
                SignatureScheme.parse(buffer), 
                Vector.parseOpaqueVector(buffer, SIGNATURE_LENGTH_BYTES));
    }

    public static CertificateVerify create(SignatureScheme algorithm, byte[] signature) {
        return new CertificateVerify(
                algorithm,
                Vector.wrap(SIGNATURE_LENGTH_BYTES, signature));
    }

}
