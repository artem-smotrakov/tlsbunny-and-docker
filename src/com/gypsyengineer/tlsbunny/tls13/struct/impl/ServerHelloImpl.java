package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.*; // TODO
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ServerHelloImpl implements ServerHello {

    private ProtocolVersion version = ProtocolVersion.TLSv13;
    private Random random = new Random();
    private CipherSuite cipher_suite = CipherSuite.TLS_AES_128_GCM_SHA256;
    private Vector<Extension> extensions = Vector.wrap(EXTENSIONS_LENGTH_BYTES);

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
    public void setProtocolVersion(ProtocolVersion version) {
        this.version = version;
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return version;
    }

    @Override
    public void setRandom(Random random) {
        this.random = random;
    }

    @Override
    public Random getRandom() {
        return random;
    }

    @Override
    public void setCipherSuite(CipherSuite cipher_suite) {
        this.cipher_suite = cipher_suite;
    }

    @Override
    public CipherSuite getCipherSuite() {
        return cipher_suite;
    }

    @Override
    public void setExtensions(Vector<Extension> extensions) {
        this.extensions = extensions;
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
    public KeyShare.ServerHello findKeyShare() throws IOException {
        return KeyShareImpl.ServerHelloImpl.parse(
                findExtension(ExtensionType.key_share).getExtensionData());
    }

    @Override
    public HandshakeType type() {
        return HandshakeType.server_hello;
    }

    public static ServerHello parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }
    
    public static ServerHello parse(ByteBuffer buffer) {
        return new ServerHelloImpl(
                ProtocolVersionImpl.parse(buffer), 
                Random.parse(buffer), 
                CipherSuiteImpl.parse(buffer),
                Vector.parse(
                    buffer,
                    EXTENSIONS_LENGTH_BYTES,
                    buf -> ExtensionImpl.parse(buf)));
    }
    
}
