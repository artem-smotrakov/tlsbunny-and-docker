/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface SignatureSchemeList extends Struct {

    int LENGTH_BYTES = 2;

    byte[] encoding() throws IOException;

    int encodingLength();

    Vector<SignatureSchemeImpl> getSupportedSignatureAlgorithms();

    void set(SignatureSchemeImpl signatureScheme);

    void setSupportedSignatureAlgorithms(Vector<SignatureSchemeImpl> supported_signature_algorithms);
    
}
