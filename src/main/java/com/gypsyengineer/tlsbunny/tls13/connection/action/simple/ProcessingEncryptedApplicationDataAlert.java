package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import static com.gypsyengineer.tlsbunny.tls13.connection.action.Phase.application_data;

public class ProcessingEncryptedApplicationDataAlert extends ProcessingEncryptedAlert {

    public ProcessingEncryptedApplicationDataAlert() {
        super(application_data);
    }
}
