package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class HelloRetryRequest implements HandshakeMessage {

    public static final int EXTENSIONS_LENGTH_BYTES = 2;
    public static final int MIN_EXTENSIONS_LENGTH = 2;
    public static final int MAX_EXTENSIONS_LENGTH = 65535;

    private ProtocolVersion server_version;
    private CipherSuite cipher_suite;
    private Vector<Extension> extensions;

    private HelloRetryRequest(ProtocolVersion server_version,
            CipherSuite cipher_suite, Vector<Extension> extensions) {

        this.server_version = server_version;
        this.cipher_suite = cipher_suite;
        this.extensions = extensions;
    }

    public void setCipherSuite(CipherSuite cipher_suite) {
        this.cipher_suite = cipher_suite;
    }

    public CipherSuite getCipherSuite() {
        return cipher_suite;
    }

    public void setProtocolVersion(ProtocolVersion server_version) {
        this.server_version = server_version;
    }

    public ProtocolVersion getProtocolVersion() {
        return server_version;
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

    public Extension getExtension(ExtensionType type) {
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

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(server_version, cipher_suite, extensions);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(server_version, cipher_suite, extensions);
    }

    @Override
    public HandshakeType type() {
        return HandshakeType.hello_retry_request;
    }

    public static HelloRetryRequest parse(ByteBuffer buffer) {
        return new HelloRetryRequest(
                ProtocolVersion.parse(buffer), 
                CipherSuite.parse(buffer), 
                Vector.parse(
                    buffer,
                    EXTENSIONS_LENGTH_BYTES,
                    buf -> Extension.parse(buf)));
    }

}
