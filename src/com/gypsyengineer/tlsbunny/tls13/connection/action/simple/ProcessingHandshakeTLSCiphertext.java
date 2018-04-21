package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.Phase;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;

public class ProcessingHandshakeTLSCiphertext extends ProcessingTLSCiphertext {

    public ProcessingHandshakeTLSCiphertext() {
        super(Phase.handshake);
        expect(ContentType.handshake);
    }
    
}
