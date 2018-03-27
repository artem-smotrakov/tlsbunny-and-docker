package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.io.IOException;

public class IncomingAlert extends AbstractReceivingAction {

    @Override
    public String name() {
        return "receiving Alert";
    }

    @Override
    void runImpl() throws Exception {
        TLSPlaintext tlsPlaintext = factory.parser().parseTLSPlaintext(buffer);
        if (!tlsPlaintext.containsAlert()) {
            output.info("received a record of %s", tlsPlaintext.getType());
            throw new IOException("expected an alert");
        }

        Alert alert = factory.parser().parseAlert(tlsPlaintext.getFragment());
        output.info("received an alert: %s", alert);
    }

    
}
