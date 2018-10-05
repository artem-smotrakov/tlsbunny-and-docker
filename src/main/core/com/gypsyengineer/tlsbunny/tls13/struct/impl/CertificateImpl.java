package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.Certificate;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.util.Objects;

public class CertificateImpl implements Certificate {

    private final Vector<Byte> certificate_request_context;
    private final Vector<CertificateEntry> certificate_list;

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
    public CertificateImpl copy() {
        return new CertificateImpl(
                (Vector<Byte>) certificate_request_context.copy(),
                (Vector<CertificateEntry>) certificate_list.copy());
    }

    @Override
    public Vector<Byte> getCertificateRequestContext() {
        return certificate_request_context;
    }

    @Override
    public Vector<CertificateEntry> getCertificateList() {
        return certificate_list;
    }

    @Override
    public HandshakeType type() {
        return HandshakeTypeImpl.certificate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CertificateImpl that = (CertificateImpl) o;
        return Objects.equals(certificate_request_context, that.certificate_request_context) &&
                Objects.equals(certificate_list, that.certificate_list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificate_request_context, certificate_list);
    }
}
