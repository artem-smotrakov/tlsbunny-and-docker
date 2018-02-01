/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateEntryImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface Certificate extends HandshakeMessage {

    int CERTIFICATE_LIST_LENGTH_BYTES = 3;
    int CONTEXT_LENGTH_BYTES = 1;

    byte[] encoding() throws IOException;

    int encodingLength();

    Vector<CertificateEntryImpl> getCertificateList();

    Vector<Byte> getCertificateRequestContext();

    void setCertificateList(Vector<CertificateEntryImpl> certificate_list);

    void setCertificateRequestContext(Vector<Byte> certificate_request_context);

    HandshakeTypeImpl type();
    
}
