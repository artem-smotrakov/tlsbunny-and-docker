/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Bytes;

/**
 *
 * @author artem
 */
public interface Finished extends HandshakeMessage {

    byte[] encoding();

    int encodingLength();

    byte[] getVerifyData();

    void setVerifyData(byte[] verify_data);

    void setVerifyData(Bytes verify_data);

    HandshakeTypeImpl type();
    
}
