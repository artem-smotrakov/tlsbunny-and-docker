package com.gypsyengineer.tlsbunny.tls13.connection;

public class EncryptedApplicationDataAlert extends EncryptedAlert {

    public EncryptedApplicationDataAlert() {
        super(Phase.application_data);
    }
}
