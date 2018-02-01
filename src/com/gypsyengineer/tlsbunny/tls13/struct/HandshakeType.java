package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;

public interface HandshakeType extends Struct {

    int ENCODING_LENGTH = 1;
    
    HandshakeType certificate = new HandshakeTypeImpl(11);
    HandshakeType certificate_request = new HandshakeTypeImpl(13);
    HandshakeType certificate_verify = new HandshakeTypeImpl(15);
    HandshakeType client_hello = new HandshakeTypeImpl(1);
    HandshakeType encrypted_extensions = new HandshakeTypeImpl(8);
    HandshakeType end_of_early_data = new HandshakeTypeImpl(5);
    HandshakeType finished = new HandshakeTypeImpl(20);
    HandshakeType hello_retry_request = new HandshakeTypeImpl(6);
    HandshakeType key_update = new HandshakeTypeImpl(24);
    HandshakeType message_hash = new HandshakeTypeImpl(254);
    HandshakeType new_session_ticket = new HandshakeTypeImpl(4);
    HandshakeType server_hello = new HandshakeTypeImpl(2);

    int getValue();
    void setValue(int value);
}
