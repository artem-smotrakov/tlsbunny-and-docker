package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls.Vector.ContentParser;
import com.gypsyengineer.tlsbunny.tls13.struct.Certificate;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CertificateImpl implements Certificate {


    private Vector<Byte> certificate_request_context;
    private Vector<CertificateEntryImpl> certificate_list;

    CertificateImpl(Vector<Byte> certificate_request_context,
            Vector<CertificateEntryImpl> certificate_list) {

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

    @Override
    public Vector<Byte> getCertificateRequestContext() {
        return certificate_request_context;
    }

    @Override
    public void setCertificateRequestContext(Vector<Byte> certificate_request_context) {
        this.certificate_request_context = certificate_request_context;
    }

    @Override
    public Vector<CertificateEntryImpl> getCertificateList() {
        return certificate_list;
    }

    @Override
    public void setCertificateList(Vector<CertificateEntryImpl> certificate_list) {
        this.certificate_list = certificate_list;
    }

    @Override
    public HandshakeTypeImpl type() {
        return HandshakeTypeImpl.certificate;
    }

    public static CertificateImpl parse(
            byte[] bytes, ContentParser certificateEntityParser) {
        
        return parse(ByteBuffer.wrap(bytes), certificateEntityParser);
    }
    
    public static CertificateImpl parse(
            ByteBuffer buffer, ContentParser certificateEntityParser) {
        
        return new CertificateImpl(
                Vector.parse(buffer,
                    CONTEXT_LENGTH_BYTES,
                    buf -> buf.get()), 
                Vector.parse(
                    buffer,
                    CERTIFICATE_LIST_LENGTH_BYTES,
                    certificateEntityParser));
    }

}
