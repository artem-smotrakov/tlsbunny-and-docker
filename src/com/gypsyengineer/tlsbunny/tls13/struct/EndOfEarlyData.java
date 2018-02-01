/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface EndOfEarlyData extends HandshakeMessage {

    byte[] encoding() throws IOException;

    int encodingLength();

    HandshakeTypeImpl type();
    
}
