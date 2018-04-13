package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

public class EncryptedApplicationDataAlert extends EncryptedAlert {

    public EncryptedApplicationDataAlert() {
        super(Phase.application_data);
    }
}
