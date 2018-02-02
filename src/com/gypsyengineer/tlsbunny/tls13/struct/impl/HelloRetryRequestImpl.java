package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.ExtensionType;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.HelloRetryRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;

public class HelloRetryRequestImpl implements HelloRetryRequest {

    private ProtocolVersion server_version;
    private CipherSuite cipher_suite;
    private Vector<Extension> extensions;

    HelloRetryRequestImpl(ProtocolVersion server_version,
            CipherSuite cipher_suite, Vector<Extension> extensions) {

        this.server_version = server_version;
        this.cipher_suite = cipher_suite;
        this.extensions = extensions;
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
    public void setProtocolVersion(ProtocolVersion server_version) {
        this.server_version = server_version;
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return server_version;
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
    public Extension getExtension(ExtensionType type) {
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

}
