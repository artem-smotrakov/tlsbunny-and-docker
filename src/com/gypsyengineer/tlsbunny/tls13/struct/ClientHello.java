package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ClientHello implements HandshakeMessage {

    public static final int LEGACY_SESSION_ID_LENGTH_BYTES = 1;
    public static final int CIPHER_SUITES_LENGTH_BYTES = 2;
    public static final int LEGACY_COMPRESSION_METHODS_LENGTH_BYTES = 1;
    public static final int EXTENSIONS_LENGTH_BYTES = 2;

    public static final int MAX_EXPECTED_SESSION_ID_BYTES = 32;
    public static final int MIN_EXPECTED_CIPHER_SUITES_BYTES = 2;
    public static final int MAX_EXPECTED_CIPHER_SUITES_BYTES = 65534;
    public static final int MAX_EXPECTED_COMPRESSION_METHODS_BYTES = 255;

    public static final int MIN_EXPECTED_EXTENSIONS_BYTES = 8;
    public static final int MAX_EXPECTED_EXTENSIONS_BYTES = 65535;

    private ProtocolVersion legacy_version = ProtocolVersion.TLSv12;
    private Random random = new Random();
    private Vector<Byte> legacy_session_id = Vector.wrap(LEGACY_SESSION_ID_LENGTH_BYTES);
    private Vector<CipherSuite> cipher_suites = Vector.wrap(CIPHER_SUITES_LENGTH_BYTES);
    private Vector<CompressionMethod> legacy_compression_methods = Vector.wrap(LEGACY_COMPRESSION_METHODS_LENGTH_BYTES);
    private Vector<Extension> extensions = Vector.wrap(EXTENSIONS_LENGTH_BYTES);

    public ClientHello(
            ProtocolVersion legacy_version,
            Random random,
            Vector<Byte> legacy_session_id,
            Vector<CipherSuite> cipher_suites,
            Vector<CompressionMethod> legacy_compression_methods,
            Vector<Extension> extensions) {

        this.legacy_version = legacy_version;
        this.random = random;
        this.legacy_session_id = legacy_session_id;
        this.cipher_suites = cipher_suites;
        this.legacy_compression_methods = legacy_compression_methods;
        this.extensions = extensions;
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(
                legacy_version,
                random,
                legacy_session_id,
                cipher_suites,
                legacy_compression_methods,
                extensions);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(
                legacy_version,
                random,
                legacy_session_id,
                cipher_suites,
                legacy_compression_methods,
                extensions);
    }

    public void setCipherSuites(Vector<CipherSuite> cipher_suites) {
        this.cipher_suites = cipher_suites;
    }

    public void setCompressionMethods(
            Vector<CompressionMethod> legacy_compression_methods) {

        this.legacy_compression_methods = legacy_compression_methods;
    }

    public Vector<Byte> getLegacySessionId() {
        return legacy_session_id;
    }

    public void setLegacySessionId(Vector<Byte> legacy_session_id) {
        this.legacy_session_id = legacy_session_id;
    }

    public void addCompressionMethod(CompressionMethod method) {
        legacy_compression_methods.add(method);
    }

    public Vector<CompressionMethod> getLegacyCompressionMethods() {
        return legacy_compression_methods;
    }

    public void addCipherSuite(CipherSuite cipher_suite) {
        cipher_suites.add(cipher_suite);
    }

    public Vector<CipherSuite> getCipherSuites() {
        return cipher_suites;
    }

    public void setProtocolVersion(ProtocolVersion legacy_version) {
        this.legacy_version = legacy_version;
    }

    public ProtocolVersion getProtocolVersion() {
        return legacy_version;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public Vector<Extension> getExtensions() {
        return extensions;
    }

    public void addExtension(Extension extension) {
        extensions.add(extension);
    }

    public void clearExtensions() {
        extensions.clear();
    }

    public void setExtensions(Vector<Extension> extensions) {
        this.extensions = extensions;
    }

    public Extension getExtension(ExtensionType type) {
        for (Extension extension : extensions.toList()) {
            if (type.equals(extension.getExtensionType())) {
                return extension;
            }
        }

        return null;
    }

    public SupportedVersions.ClientHello getSupportedVersions() throws IOException {
        return SupportedVersions.ClientHello.parse(
                getExtension(ExtensionType.SUPPORTED_VERSIONS).getExtensionData());
    }

    public SignatureSchemeList getSignatureAlgorithms() throws IOException {
        return SignatureSchemeList.parse(
                getExtension(ExtensionType.SIGNATURE_ALGORITHMS).getExtensionData());
    }

    public NamedGroupList getSupportedGroups() throws IOException {
        return NamedGroupList.parse(
                getExtension(ExtensionType.SUPPORTED_GROUPS).getExtensionData());
    }

    public KeyShare.ClientHello getKeyShare() throws IOException {
        return KeyShare.ClientHello.parse(
                getExtension(ExtensionType.KEY_SHARE).getExtensionData());
    }

    @Override
    public HandshakeType type() {
        return HandshakeType.CLIENT_HELLO;
    }

    public static ClientHello parse(byte[] data) {
        return parse(ByteBuffer.wrap(data));
    }

    public static ClientHello parse(ByteBuffer buffer) {
        ProtocolVersion legacy_version = ProtocolVersion.parse(buffer);
        Random random = Random.parse(buffer);
        Vector<Byte> legacy_session_id = Vector.parse(
                buffer, 
                LEGACY_SESSION_ID_LENGTH_BYTES, 
                buf -> buf.get());
        Vector<CipherSuite> cipher_suites = Vector.parse(
                buffer, 
                CIPHER_SUITES_LENGTH_BYTES, 
                buf -> CipherSuite.parse(buf));
        Vector<CompressionMethod> legacy_compression_methods = Vector.parse(
                buffer, 
                LEGACY_COMPRESSION_METHODS_LENGTH_BYTES, 
                buf -> CompressionMethod.parse(buf));
        Vector<Extension> extensions = Vector.parse(
                buffer,
                EXTENSIONS_LENGTH_BYTES,
                buf -> Extension.parse(buf));

        return new ClientHello(legacy_version, random, legacy_session_id, 
                cipher_suites, legacy_compression_methods, extensions);
    }

}
