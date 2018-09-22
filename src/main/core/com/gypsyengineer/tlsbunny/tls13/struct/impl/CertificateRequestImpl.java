package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;

public class CertificateRequestImpl implements CertificateRequest {

    private final Vector<Byte> certificate_request_context;
    private final Vector<Extension> extensions;

    CertificateRequestImpl(Vector<Byte> certificate_request_context, 
                           Vector<Extension> extensions) {

        this.certificate_request_context = certificate_request_context;
        this.extensions = extensions;
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(certificate_request_context, extensions);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(certificate_request_context, extensions);
    }

    @Override
    public Vector<Byte> getCertificateRequestContext() {
        return certificate_request_context;
    }

    @Override
    public Vector<Extension> getExtensions() {
        return extensions;
    }

    @Override
    public HandshakeType type() {
        return HandshakeTypeImpl.certificate_request;
    }
    
}
