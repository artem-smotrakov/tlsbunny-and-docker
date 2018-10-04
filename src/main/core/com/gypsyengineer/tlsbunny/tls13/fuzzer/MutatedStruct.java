package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.*;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.io.IOException;

public class MutatedStruct implements TLSPlaintext, Handshake, ChangeCipherSpec,
        ClientHello, Finished, Certificate, CertificateVerify {

    private static final HandshakeType NO_HANDSHAKE_TYPE = null;

    private final int mutatedEncodingLength;
    private final byte[] mutatedEncoding;
    private final HandshakeType handshakeType;

    public MutatedStruct(byte[] mutatedEncoding) {
        this(mutatedEncoding.length, mutatedEncoding, NO_HANDSHAKE_TYPE);
    }

    public MutatedStruct(int mutatedEncodingLength, byte[] mutatedEncoding) {
        this(mutatedEncodingLength, mutatedEncoding, NO_HANDSHAKE_TYPE);
    }

    public MutatedStruct(int mutatedEncodingLength, byte[] mutatedEncoding,
            HandshakeType handshakeType) {

        this.mutatedEncodingLength = mutatedEncodingLength;
        this.mutatedEncoding = mutatedEncoding;
        this.handshakeType = handshakeType;
    }

    // Struct

    @Override
    public int encodingLength() {
        return mutatedEncodingLength;
    }

    @Override
    public byte[] encoding() throws IOException {
        return mutatedEncoding;
    }

    @Override
    public Struct copy() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    // TLSPlaintext

    @Override
    public boolean containsAlert() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    @Override
    public boolean containsApplicationData() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    @Override
    public boolean containsHandshake() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    @Override
    public boolean containsChangeCipherSpec() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getFragment() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    @Override
    public ProtocolVersion getLegacyRecordVersion() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    @Override
    public ContentType getType() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    @Override
    public boolean containsCertificate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsCertificateRequest() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsCertificateVerify() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsClientHello() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsEncryptedExtensions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsFinished() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsHelloRetryRequest() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsNewSessionTicket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsServerHello() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getBody() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HandshakeType getMessageType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public UInt16 getFragmentLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public UInt24 getBodyLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HandshakeType type() {
        return handshakeType;
    }

    @Override
    public Extension findExtension(ExtensionType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Vector<CipherSuite> getCipherSuites() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Vector<Extension> getExtensions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Vector<CompressionMethod> getLegacyCompressionMethods() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Vector<Byte> getLegacySessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Random getRandom() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getVerifyData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Vector<CertificateEntry> getCertificateList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Vector<Byte> getCertificateRequestContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SignatureScheme getAlgorithm() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Vector<Byte> getSignature() {
        throw new UnsupportedOperationException();
    }

    // ChangeCipherSpec

    @Override
    public int getValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException();
    }
}
