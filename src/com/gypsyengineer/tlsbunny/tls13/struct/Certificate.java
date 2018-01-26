package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls.Vector.ContentParser;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Certificate implements Entity, HandshakeMessage {

    public static final int CONTEXT_LENGTH_BYTES = 1;
    public static final int CERTIFICATE_LIST_LENGTH_BYTES = 3;

    private Vector<Byte> certificate_request_context;
    private Vector<CertificateEntry> certificate_list;

    private Certificate(Vector<Byte> certificate_request_context,
            Vector<CertificateEntry> certificate_list) {

        this.certificate_request_context = certificate_request_context;
        this.certificate_list = certificate_list;
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(certificate_request_context, certificate_list);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(certificate_request_context, certificate_list);
    }

    public Vector<Byte> getCertificateRequestContext() {
        return certificate_request_context;
    }

    public void setCertificateRequestContext(Vector<Byte> certificate_request_context) {
        this.certificate_request_context = certificate_request_context;
    }

    public Vector<CertificateEntry> getCertificateList() {
        return certificate_list;
    }

    public void setCertificateList(Vector<CertificateEntry> certificate_list) {
        this.certificate_list = certificate_list;
    }

    @Override
    public HandshakeType type() {
        return HandshakeType.certificate;
    }

    public static Certificate parse(
            ByteBuffer buffer, ContentParser certificateEntiryParser) {
        
        return new Certificate(
                Vector.parse(buffer,
                    CONTEXT_LENGTH_BYTES,
                    buf -> buf.get()), 
                Vector.parse(
                    buffer,
                    CERTIFICATE_LIST_LENGTH_BYTES,
                    certificateEntiryParser));
    }

}
