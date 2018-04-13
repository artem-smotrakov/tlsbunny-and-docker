package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

public class ProcessingEncryptedHandshakeAlert extends ProcessingEncryptedAlert {

    public ProcessingEncryptedHandshakeAlert() {
        super(Phase.handshake);
    }
}
