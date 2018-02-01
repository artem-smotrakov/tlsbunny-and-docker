package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ServerHelloImpl implements ServerHello {


    private ProtocolVersionImpl version = ProtocolVersionImpl.TLSv13;
    private Random random = new Random();
    private CipherSuiteImpl cipher_suite = CipherSuiteImpl.TLS_AES_128_GCM_SHA256;
    private Vector<ExtensionImpl> extensions = Vector.wrap(EXTENSIONS_LENGTH_BYTES);

    ServerHelloImpl(ProtocolVersionImpl version, Random random,
            CipherSuiteImpl cipher_suite, Vector<ExtensionImpl> extensions) {

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
    public void setProtocolVersion(ProtocolVersionImpl version) {
        this.version = version;
    }

    @Override
    public ProtocolVersionImpl getProtocolVersion() {
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
    public void setCipherSuite(CipherSuiteImpl cipher_suite) {
        this.cipher_suite = cipher_suite;
    }

    @Override
    public CipherSuiteImpl getCipherSuite() {
        return cipher_suite;
    }

    @Override
    public void setExtensions(Vector<ExtensionImpl> extensions) {
        this.extensions = extensions;
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
    public ExtensionImpl findExtension(ExtensionTypeImpl type) {
        for (ExtensionImpl extension : extensions.toList()) {
            if (type.equals(extension.getExtensionType())) {
                return extension;
            }
        }

        return null;
    }

    @Override
    public Vector<ExtensionImpl> getExtensions() {
        return extensions;
    }

    @Override
    public KeyShareImpl.ServerHelloImpl findKeyShare() throws IOException {
        return KeyShareImpl.ServerHelloImpl.parse(
                findExtension(ExtensionTypeImpl.key_share).getExtensionData());
    }

    @Override
    public HandshakeTypeImpl type() {
        return HandshakeTypeImpl.server_hello;
    }

    public static ServerHelloImpl parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }
    
    public static ServerHelloImpl parse(ByteBuffer buffer) {
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
