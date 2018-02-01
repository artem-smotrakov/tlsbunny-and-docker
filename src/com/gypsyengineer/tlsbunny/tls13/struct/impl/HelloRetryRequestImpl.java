package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.HelloRetryRequest;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class HelloRetryRequestImpl implements HelloRetryRequest {


    private ProtocolVersionImpl server_version;
    private CipherSuiteImpl cipher_suite;
    private Vector<ExtensionImpl> extensions;

    HelloRetryRequestImpl(ProtocolVersionImpl server_version,
            CipherSuiteImpl cipher_suite, Vector<ExtensionImpl> extensions) {

        this.server_version = server_version;
        this.cipher_suite = cipher_suite;
        this.extensions = extensions;
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
    public void setProtocolVersion(ProtocolVersionImpl server_version) {
        this.server_version = server_version;
    }

    @Override
    public ProtocolVersionImpl getProtocolVersion() {
        return server_version;
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
    public ExtensionImpl getExtension(ExtensionTypeImpl type) {
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
    public int encodingLength() {
        return Utils.getEncodingLength(server_version, cipher_suite, extensions);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(server_version, cipher_suite, extensions);
    }

    @Override
    public HandshakeTypeImpl type() {
        return HandshakeTypeImpl.hello_retry_request;
    }

    public static HelloRetryRequestImpl parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }
    
    public static HelloRetryRequestImpl parse(ByteBuffer buffer) {
        return new HelloRetryRequestImpl(
                ProtocolVersionImpl.parse(buffer), 
                CipherSuiteImpl.parse(buffer), 
                Vector.parse(
                    buffer,
                    EXTENSIONS_LENGTH_BYTES,
                    buf -> ExtensionImpl.parse(buf)));
    }

}
