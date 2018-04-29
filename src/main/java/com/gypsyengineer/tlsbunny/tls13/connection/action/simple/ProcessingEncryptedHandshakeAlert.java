package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import static com.gypsyengineer.tlsbunny.tls13.connection.action.Phase.handshake;

public class ProcessingEncryptedHandshakeAlert extends ProcessingEncryptedAlert {

    public ProcessingEncryptedHandshakeAlert() {
        super(handshake);
    }
}
