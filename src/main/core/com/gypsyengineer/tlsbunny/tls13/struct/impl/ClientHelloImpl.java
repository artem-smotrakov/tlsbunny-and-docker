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
import java.util.Objects;

public class ClientHelloImpl implements ClientHello {
    
    private final ProtocolVersion legacy_version;
    private final Random random;
    private final Vector<Byte> legacy_session_id;
    private final Vector<CipherSuite> cipher_suites;
    private final Vector<CompressionMethod> legacy_compression_methods;
    private final Vector<Extension> extensions;

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
    public ClientHelloImpl copy() {
        return new ClientHelloImpl(
                (ProtocolVersion) legacy_version.copy(),
                (Random) random.copy(),
                (Vector<Byte>) legacy_session_id.copy(),
                (Vector<CipherSuite>) cipher_suites.copy(),
                (Vector<CompressionMethod>) legacy_compression_methods.copy(),
                (Vector<Extension>) extensions.copy()
        );
    }

    @Override
    public Vector<Byte> getLegacySessionId() {
        return legacy_session_id;
    }

    @Override
    public Vector<CompressionMethod> getLegacyCompressionMethods() {
        return legacy_compression_methods;
    }

    @Override
    public Vector<CipherSuite> getCipherSuites() {
        return cipher_suites;
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
    public Vector<Extension> getExtensions() {
        return extensions;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientHelloImpl that = (ClientHelloImpl) o;
        return Objects.equals(legacy_version, that.legacy_version) &&
                Objects.equals(random, that.random) &&
                Objects.equals(legacy_session_id, that.legacy_session_id) &&
                Objects.equals(cipher_suites, that.cipher_suites) &&
                Objects.equals(legacy_compression_methods, that.legacy_compression_methods) &&
                Objects.equals(extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(legacy_version, random, legacy_session_id,
                cipher_suites, legacy_compression_methods, extensions);
    }
}
