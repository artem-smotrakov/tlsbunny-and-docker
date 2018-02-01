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
public interface EncryptedExtensions extends HandshakeMessage {

    int EXTENSIONS_LENGTH_BYTES = 2;

    byte[] encoding() throws IOException;

    int encodingLength();

    Vector<ExtensionImpl> getExtensions();

    void setExtensions(Vector<ExtensionImpl> extensions);

    HandshakeTypeImpl type();
    
}
