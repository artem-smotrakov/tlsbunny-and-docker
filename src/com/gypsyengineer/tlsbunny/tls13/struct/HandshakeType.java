/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;

/**
 *
 * @author artem
 */
public interface HandshakeType extends Struct {

    int ENCODING_LENGTH = 1;
    HandshakeTypeImpl certificate = new HandshakeTypeImpl(11);
    HandshakeTypeImpl certificate_request = new HandshakeTypeImpl(13);
    HandshakeTypeImpl certificate_verify = new HandshakeTypeImpl(15);
    HandshakeTypeImpl client_hello = new HandshakeTypeImpl(1);
    HandshakeTypeImpl encrypted_extensions = new HandshakeTypeImpl(8);
    HandshakeTypeImpl end_of_early_data = new HandshakeTypeImpl(5);
    HandshakeTypeImpl finished = new HandshakeTypeImpl(20);
    HandshakeTypeImpl hello_retry_request = new HandshakeTypeImpl(6);
    HandshakeTypeImpl key_update = new HandshakeTypeImpl(24);
    HandshakeTypeImpl message_hash = new HandshakeTypeImpl(254);
    HandshakeTypeImpl new_session_ticket = new HandshakeTypeImpl(4);
    HandshakeTypeImpl server_hello = new HandshakeTypeImpl(2);

    byte[] encoding();

    int encodingLength();

    boolean equals(Object obj);

    int getValue();

    int hashCode();

    void setValue(int value);
    
}
