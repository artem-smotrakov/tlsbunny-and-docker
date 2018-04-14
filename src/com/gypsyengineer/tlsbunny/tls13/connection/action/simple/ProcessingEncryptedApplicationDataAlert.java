package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

public class ProcessingEncryptedApplicationDataAlert extends ProcessingEncryptedAlert {

    public ProcessingEncryptedApplicationDataAlert() {
        super(Phase.application_data);
    }
}
