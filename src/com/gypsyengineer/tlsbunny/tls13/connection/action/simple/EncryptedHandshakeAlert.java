package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

public class EncryptedHandshakeAlert extends EncryptedAlert {

    public EncryptedHandshakeAlert() {
        super(Phase.handshake);
    }
}
