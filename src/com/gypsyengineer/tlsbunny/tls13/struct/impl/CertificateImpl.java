package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.Certificate;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;

public class CertificateImpl implements Certificate {

    private Vector<Byte> certificate_request_context;
    private Vector<CertificateEntry> certificate_list;

    CertificateImpl(Vector<Byte> certificate_request_context,
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

    @Override
    public Vector<Byte> getCertificateRequestContext() {
        return certificate_request_context;
    }

    @Override
    public void setCertificateRequestContext(Vector<Byte> certificate_request_context) {
        this.certificate_request_context = certificate_request_context;
    }

    @Override
    public Vector<CertificateEntry> getCertificateList() {
        return certificate_list;
    }

    @Override
    public void setCertificateList(Vector<CertificateEntry> certificate_list) {
        this.certificate_list = certificate_list;
    }

    @Override
    public HandshakeType type() {
        return HandshakeTypeImpl.certificate;
    }

}
