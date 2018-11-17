package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.util.List;

public class FuzzedStruct implements ClientHello, ServerHello, 
        HelloRetryRequest, EncryptedExtensions, EndOfEarlyData, Certificate, 
        CertificateRequest, CertificateVerify, Finished, 
        ProtocolVersion, CipherSuite, Vector, CompressionMethod, 
        Extension, ExtensionType, Random {

    private final int encodingLength;
    private final byte[] encoding;

    public static FuzzedStruct fuzzedHandshakeMessage(byte[] encoding) {
        return new FuzzedStruct(encoding.length, encoding);
    }

    public static FuzzedStruct fuzzedHandshakeMessage(
            int encodingLength, byte[] encoding) {

        return new FuzzedStruct(encodingLength, encoding);
    }

    private FuzzedStruct(int encodingLength, byte[] encoding) {
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
        return new FuzzedStruct(encodingLength, encoding);
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
    public Vector<Byte> certificateRequestContext() {
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
    public Vector<CipherSuite> cipherSuites() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Vector<CompressionMethod> legacyCompressionMethods() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Vector<Byte> legacySessionId() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public ProtocolVersion protocolVersion() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Random random() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public void cipherSuites(Vector<CipherSuite> cipherSuites) {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public void extensions(Vector<Extension> extensions) {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public void legacySessionIdEcho(Vector<Byte> echo) {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public void legacyCompressionMethods(Vector<CompressionMethod> compressionMethods) {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public void legacySessionId(Vector<Byte> sessionId) {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Vector<Byte> legacySessionIdEcho() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public CipherSuite cipherSuite() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public CompressionMethod legacyCompressionMethod() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Vector<Extension> extensions() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Extension find(ExtensionType type) {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public byte[] getVerifyData() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Object get(int index) {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Object first() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public void add(Object object) {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public void set(int index, Object object) {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public List toList() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public int lengthBytes() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public byte[] bytes() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public AEAD.Method cipher() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public int getFirst() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public int getSecond() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public String hash() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public int hashLength() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public int ivLength() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public int keyLength() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public int getCode() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public Vector<Byte> getExtensionData() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public ExtensionType getExtensionType() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public int getMinor() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public int getMajor() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public byte[] getBytes() {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public void setBytes(byte[] bytes) {
        throw new UnsupportedOperationException("I can't do that!");
    }

    @Override
    public void setLastBytes(byte[] lastBytes) {
        throw new UnsupportedOperationException("I can't do that!");
    }
}
