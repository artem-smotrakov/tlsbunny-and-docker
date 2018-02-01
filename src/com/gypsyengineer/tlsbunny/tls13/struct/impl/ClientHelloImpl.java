package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupListImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ProtocolVersionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.KeyShareImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeListImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SupportedVersionsImpl;
import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.ClientHello;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ClientHelloImpl implements ClientHello {
    
    
    private ProtocolVersionImpl legacy_version = ProtocolVersionImpl.TLSv12;
    private Random random = new Random();
    private Vector<Byte> legacy_session_id = Vector.wrap(LEGACY_SESSION_ID_LENGTH_BYTES);
    private Vector<CipherSuiteImpl> cipher_suites = Vector.wrap(CIPHER_SUITES_LENGTH_BYTES);
    private Vector<CompressionMethodImpl> legacy_compression_methods = Vector.wrap(LEGACY_COMPRESSION_METHODS_LENGTH_BYTES);
    private Vector<ExtensionImpl> extensions = Vector.wrap(EXTENSIONS_LENGTH_BYTES);

    ClientHelloImpl(
            ProtocolVersionImpl legacy_version,
            Random random,
            Vector<Byte> legacy_session_id,
            Vector<CipherSuiteImpl> cipher_suites,
            Vector<CompressionMethodImpl> legacy_compression_methods,
            Vector<ExtensionImpl> extensions) {

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

    @Override
    public void setCipherSuites(Vector<CipherSuiteImpl> cipher_suites) {
        this.cipher_suites = cipher_suites;
    }

    @Override
    public void setCompressionMethods(
            Vector<CompressionMethodImpl> legacy_compression_methods) {

        this.legacy_compression_methods = legacy_compression_methods;
    }

    @Override
    public Vector<Byte> getLegacySessionId() {
        return legacy_session_id;
    }

    @Override
    public void setLegacySessionId(Vector<Byte> legacy_session_id) {
        this.legacy_session_id = legacy_session_id;
    }

    @Override
    public void addCompressionMethod(CompressionMethodImpl method) {
        legacy_compression_methods.add(method);
    }

    @Override
    public Vector<CompressionMethodImpl> getLegacyCompressionMethods() {
        return legacy_compression_methods;
    }

    @Override
    public void addCipherSuite(CipherSuiteImpl cipher_suite) {
        cipher_suites.add(cipher_suite);
    }

    @Override
    public Vector<CipherSuiteImpl> getCipherSuites() {
        return cipher_suites;
    }

    @Override
    public void setProtocolVersion(ProtocolVersionImpl legacy_version) {
        this.legacy_version = legacy_version;
    }

    @Override
    public ProtocolVersionImpl getProtocolVersion() {
        return legacy_version;
    }

    @Override
    public Random getRandom() {
        return random;
    }

    @Override
    public void setRandom(Random random) {
        this.random = random;
    }

    @Override
    public Vector<ExtensionImpl> getExtensions() {
        return extensions;
    }

    @Override
    public void addExtension(ExtensionImpl extension) {
        extensions.add(extension);
    }

    @Override
    public void clearExtensions() {
        extensions.clear();
    }

    @Override
    public void setExtensions(Vector<ExtensionImpl> extensions) {
        this.extensions = extensions;
    }

    @Override
    public ExtensionImpl findExtension(ExtensionTypeImpl type) {
        for (ExtensionImpl extension : extensions.toList()) {
            if (type.equals(extension.getExtensionType())) {
                return extension;
            }
        }

        return null;
    }

    @Override
    public SupportedVersionsImpl.ClientHelloImpl findSupportedVersions() throws IOException {
        return SupportedVersionsImpl.ClientHelloImpl.parse(
                findExtension(ExtensionTypeImpl.supported_versions).getExtensionData());
    }

    @Override
    public SignatureSchemeListImpl findSignatureAlgorithms() throws IOException {
        return SignatureSchemeListImpl.parse(
                findExtension(ExtensionTypeImpl.signature_algorithms).getExtensionData());
    }

    @Override
    public NamedGroupListImpl findSupportedGroups() throws IOException {
        return NamedGroupListImpl.parse(
                findExtension(ExtensionTypeImpl.supported_groups).getExtensionData());
    }

    @Override
    public KeyShareImpl.ClientHelloImpl findKeyShare() throws IOException {
        return KeyShareImpl.ClientHelloImpl.parse(
                findExtension(ExtensionTypeImpl.key_share).getExtensionData());
    }

    @Override
    public HandshakeTypeImpl type() {
        return HandshakeTypeImpl.client_hello;
    }

    public static ClientHelloImpl parse(byte[] data) {
        return parse(ByteBuffer.wrap(data));
    }

    public static ClientHelloImpl parse(ByteBuffer buffer) {
        ProtocolVersionImpl legacy_version = ProtocolVersionImpl.parse(buffer);
        Random random = Random.parse(buffer);
        Vector<Byte> legacy_session_id = Vector.parse(
                buffer, 
                LEGACY_SESSION_ID_LENGTH_BYTES, 
                buf -> buf.get());
        Vector<CipherSuiteImpl> cipher_suites = Vector.parse(
                buffer, 
                CIPHER_SUITES_LENGTH_BYTES, 
                buf -> CipherSuiteImpl.parse(buf));
        Vector<CompressionMethodImpl> legacy_compression_methods = Vector.parse(
                buffer, 
                LEGACY_COMPRESSION_METHODS_LENGTH_BYTES, 
                buf -> CompressionMethodImpl.parse(buf));
        Vector<ExtensionImpl> extensions = Vector.parse(
                buffer,
                EXTENSIONS_LENGTH_BYTES,
                buf -> ExtensionImpl.parse(buf));

        return new ClientHelloImpl(legacy_version, random, legacy_session_id, 
                cipher_suites, legacy_compression_methods, extensions);
    }

}
