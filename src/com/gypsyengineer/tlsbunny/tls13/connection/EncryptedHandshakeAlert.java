package com.gypsyengineer.tlsbunny.tls13.connection;

public class EncryptedHandshakeAlert extends EncryptedAlert {

    public EncryptedHandshakeAlert() {
        super(Phase.handshake);
    }
}
