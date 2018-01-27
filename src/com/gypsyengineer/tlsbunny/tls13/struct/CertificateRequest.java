package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CertificateRequest implements HandshakeMessage {

    public static final int CERTIFICATE_REQUEST_CONTEXT_LENGTH_BYTES = 1;
    public static final int EXTENSIONS_LENGTH_BYTES = 2;

    private Vector<Byte> certificate_request_context;
    private Vector<Extension> extensions;

    public CertificateRequest(Vector<Byte> certificate_request_context, 
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

    public Vector<Byte> getCertificateRequestContext() {
        return certificate_request_context;
    }

    public void setCertificateRequestContext(Vector<Byte> certificate_request_context) {
        this.certificate_request_context = certificate_request_context;
    }

    public Vector<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(Vector<Extension> extensions) {
        this.extensions = extensions;
    }

    @Override
    public HandshakeType type() {
        return HandshakeType.certificate_request;
    }
    
    public static CertificateRequest parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }

    public static CertificateRequest parse(ByteBuffer buffer) {
        return new CertificateRequest(Vector.parse(
                    buffer,
                    CERTIFICATE_REQUEST_CONTEXT_LENGTH_BYTES,
                    buf -> buf.get()), 
                Vector.parse(
                    buffer,
                    EXTENSIONS_LENGTH_BYTES,
                    buf -> Extension.parse(buf)));
    }

}
