package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

public class FuzzedHandshakeMessage implements ClientHello, ServerHello, 
        HelloRetryRequest, EncryptedExtensions, EndOfEarlyData, Certificate, 
        CertificateRequest, CertificateVerify, Finished {

    private final int encodingLength;
    private final byte[] encoding;

    public static FuzzedHandshakeMessage fuzzedHandshakeMessage(byte[] encoding) {
        return new FuzzedHandshakeMessage(encoding.length, encoding);
    }

    public static FuzzedHandshakeMessage fuzzedHandshakeMessage(
            int encodingLength, byte[] encoding) {

        return new FuzzedHandshakeMessage(encodingLength, encoding);
    }

    private FuzzedHandshakeMessage(int encodingLength, byte[] encoding) {
        this.encodingLength = encodingLength;
        this.encoding = encoding.clone();
    }

    @Override
    public int encodingLength() {
        return encodingLength;
    }

    @Override
    public byte[] encoding() {
        return encoding.clone();
    }

    @Override
    public Struct copy() {
        return new FuzzedHandshakeMessage(encodingLength, encoding);
    }

    @Override
    public HandshakeType type() {
        throw new UnsupportedOperationException("I don't know my type!");
    }

    // implement interfaces of handshake messages
    
    @Override
    public Vector<CertificateEntry> getCertificateList() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Vector<Byte> getCertificateRequestContext() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public SignatureScheme getAlgorithm() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Vector<Byte> getSignature() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Extension findExtension(ExtensionType type) {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Vector<CipherSuite> getCipherSuites() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Vector<Extension> getExtensions() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Vector<CompressionMethod> getLegacyCompressionMethods() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Vector<Byte> getLegacySessionId() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Random getRandom() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Vector<Byte> getLegacySessionIdEcho() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public CipherSuite getCipherSuite() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public CompressionMethod getLegacyCompressionMethod() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public byte[] getVerifyData() {
        throw new UnsupportedOperationException("I can't do that!");
    }
}
