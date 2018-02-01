package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.ClientHello;
import com.gypsyengineer.tlsbunny.tls13.struct.CompressionMethod;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.ExtensionType;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;

public class ClientHelloImpl implements ClientHello {
    
    private ProtocolVersion legacy_version = ProtocolVersionImpl.TLSv12;
    private Random random = new Random();
    private Vector<Byte> legacy_session_id = Vector.wrap(LEGACY_SESSION_ID_LENGTH_BYTES);
    private Vector<CipherSuite> cipher_suites = Vector.wrap(CIPHER_SUITES_LENGTH_BYTES);
    private Vector<CompressionMethod> legacy_compression_methods = Vector.wrap(LEGACY_COMPRESSION_METHODS_LENGTH_BYTES);
    private Vector<Extension> extensions = Vector.wrap(EXTENSIONS_LENGTH_BYTES);

    ClientHelloImpl(
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

    @Override
    public void setCipherSuites(Vector<CipherSuite> cipher_suites) {
        this.cipher_suites = cipher_suites;
    }

    @Override
    public void setCompressionMethods(
            Vector<CompressionMethod> legacy_compression_methods) {

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
    public void addCompressionMethod(CompressionMethod method) {
        legacy_compression_methods.add(method);
    }

    @Override
    public Vector<CompressionMethod> getLegacyCompressionMethods() {
        return legacy_compression_methods;
    }

    @Override
    public void addCipherSuite(CipherSuite cipher_suite) {
        cipher_suites.add(cipher_suite);
    }

    @Override
    public Vector<CipherSuite> getCipherSuites() {
        return cipher_suites;
    }

    @Override
    public void setProtocolVersion(ProtocolVersion legacy_version) {
        this.legacy_version = legacy_version;
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
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
    public Vector<Extension> getExtensions() {
        return extensions;
    }

    @Override
    public void addExtension(Extension extension) {
        extensions.add(extension);
    }

    @Override
    public void clearExtensions() {
        extensions.clear();
    }

    @Override
    public void setExtensions(Vector<Extension> extensions) {
        this.extensions = extensions;
    }

    @Override
    public Extension findExtension(ExtensionType type) {
        for (Extension extension : extensions.toList()) {
            if (type.equals(extension.getExtensionType())) {
                return extension;
            }
        }

        return null;
    }

    @Override
    public HandshakeType type() {
        return HandshakeTypeImpl.client_hello;
    }

}
