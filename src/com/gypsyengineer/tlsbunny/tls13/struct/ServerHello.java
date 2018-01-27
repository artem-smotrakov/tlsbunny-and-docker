package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ServerHello implements HandshakeMessage {

    public static final int EXTENSIONS_LENGTH_BYTES = 2;

    private ProtocolVersion version = ProtocolVersion.TLSv13;
    private Random random = new Random();
    private CipherSuite cipher_suite = CipherSuite.TLS_AES_128_GCM_SHA256;
    private Vector<Extension> extensions = Vector.wrap(EXTENSIONS_LENGTH_BYTES);

    private ServerHello(ProtocolVersion version, Random random,
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

    public void setProtocolVersion(ProtocolVersion version) {
        this.version = version;
    }

    public ProtocolVersion getProtocolVersion() {
        return version;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public Random getRandom() {
        return random;
    }

    public void setCipherSuite(CipherSuite cipher_suite) {
        this.cipher_suite = cipher_suite;
    }

    public CipherSuite getCipherSuite() {
        return cipher_suite;
    }

    public void setExtensions(Vector<Extension> extensions) {
        this.extensions = extensions;
    }

    public void addExtension(Extension extension) {
        extensions.add(extension);
    }

    public void clearExtensions() {
        extensions.clear();
    }

    public Extension findExtension(ExtensionType type) {
        for (Extension extension : extensions.toList()) {
            if (type.equals(extension.getExtensionType())) {
                return extension;
            }
        }

        return null;
    }

    public Vector<Extension> getExtensions() {
        return extensions;
    }

    public KeyShare.ServerHello findKeyShare() throws IOException {
        return KeyShare.ServerHello.parse(
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
        return new ServerHello(
                ProtocolVersion.parse(buffer), 
                Random.parse(buffer), 
                CipherSuite.parse(buffer), 
                Vector.parse(
                    buffer,
                    EXTENSIONS_LENGTH_BYTES,
                    buf -> Extension.parse(buf)));
    }
}
