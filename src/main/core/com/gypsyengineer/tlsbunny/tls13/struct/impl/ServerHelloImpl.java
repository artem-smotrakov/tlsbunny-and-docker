package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.CompressionMethod;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.ExtensionType;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.util.Objects;

public class ServerHelloImpl implements ServerHello {

    protected final ProtocolVersion version;
    protected final Random random;
    protected final Vector<Byte> legacy_session_id_echo;
    protected final CipherSuite cipher_suite;
    protected final CompressionMethod legacy_compression_method;
    protected final Vector<Extension> extensions;

    ServerHelloImpl(ProtocolVersion version, Random random,
            Vector<Byte> legacy_session_id_echo,
            CipherSuite cipher_suite,
            CompressionMethod legacy_compression_method,
            Vector<Extension> extensions) {

        this.version = version;
        this.random = random;
        this.legacy_session_id_echo = legacy_session_id_echo;
        this.cipher_suite = cipher_suite;
        this.legacy_compression_method = legacy_compression_method;
        this.extensions = extensions;
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(
                version,
                random,
                legacy_session_id_echo,
                cipher_suite,
                legacy_compression_method,
                extensions);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(
                version,
                random,
                legacy_session_id_echo,
                cipher_suite,
                legacy_compression_method,
                extensions);
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return version;
    }

    @Override
    public Random getRandom() {
        return random;
    }

    @Override
    public Vector<Byte> getLegacySessionIdEcho() {
        return legacy_session_id_echo;
    }

    @Override
    public CipherSuite getCipherSuite() {
        return cipher_suite;
    }

    @Override
    public CompressionMethod getLegacyCompressionMethod() {
        return legacy_compression_method;
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
    public Vector<Extension> getExtensions() {
        return extensions;
    }

    @Override
    public HandshakeType type() {
        return HandshakeType.server_hello;
    }

    @Override
    public ServerHelloImpl copy() {
        return new ServerHelloImpl(
                (ProtocolVersion) version.copy(),
                random.copy(),
                (Vector<Byte>) legacy_session_id_echo.copy(),
                (CipherSuite) cipher_suite.copy(),
                (CompressionMethod) legacy_compression_method.copy(),
                (Vector<Extension>) extensions.copy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerHelloImpl that = (ServerHelloImpl) o;
        return Objects.equals(version, that.version) &&
                Objects.equals(random, that.random) &&
                Objects.equals(legacy_session_id_echo, that.legacy_session_id_echo) &&
                Objects.equals(cipher_suite, that.cipher_suite) &&
                Objects.equals(legacy_compression_method, that.legacy_compression_method) &&
                Objects.equals(extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, random, legacy_session_id_echo,
                cipher_suite, legacy_compression_method, extensions);
    }
}
