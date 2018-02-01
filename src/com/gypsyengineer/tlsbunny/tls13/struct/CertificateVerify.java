/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeImpl;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface CertificateVerify extends HandshakeMessage {

    int SIGNATURE_LENGTH_BYTES = 2;

    byte[] encoding() throws IOException;

    int encodingLength();

    SignatureSchemeImpl getAlgorithm();

    Vector<Byte> getSignature();

    void setAlgorithm(SignatureSchemeImpl algorithm);

    void setSignature(Vector<Byte> signature);

    HandshakeTypeImpl type();
    
}
