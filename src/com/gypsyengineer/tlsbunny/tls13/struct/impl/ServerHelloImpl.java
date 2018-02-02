package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.ExtensionType;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;

public class ServerHelloImpl implements ServerHello {

    private final ProtocolVersion version;
    private final Random random;
    private final CipherSuite cipher_suite;
    private final Vector<Extension> extensions;

    ServerHelloImpl(ProtocolVersion version, Random random,
            CipherSuite cipher_suite, Vector<Extension> extensions) {

        this.version = version;
        this.random = random;
        this.cipher_suite = cipher_suite;
        this.extensions = extensions;
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(
                version,
                random,
                cipher_suite,
                extensions);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(
                version,
                random,
                cipher_suite,
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
    public CipherSuite getCipherSuite() {
        return cipher_suite;
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

}
