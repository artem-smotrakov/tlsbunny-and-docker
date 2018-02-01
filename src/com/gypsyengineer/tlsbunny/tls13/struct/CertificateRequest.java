/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionImpl;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface CertificateRequest extends HandshakeMessage {

    int CERTIFICATE_REQUEST_CONTEXT_LENGTH_BYTES = 1;
    int EXTENSIONS_LENGTH_BYTES = 2;

    byte[] encoding() throws IOException;

    int encodingLength();

    Vector<Byte> getCertificateRequestContext();

    Vector<ExtensionImpl> getExtensions();

    void setCertificateRequestContext(Vector<Byte> certificate_request_context);

    void setExtensions(Vector<ExtensionImpl> extensions);

    HandshakeTypeImpl type();
    
}
