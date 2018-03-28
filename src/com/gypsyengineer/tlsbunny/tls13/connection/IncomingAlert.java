package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.io.IOException;

public class IncomingAlert extends AbstractAction {

    @Override
    public String name() {
        return "receiving Alert";
    }

    @Override
    public Action run() throws Exception {
        TLSPlaintext tlsPlaintext = factory.parser().parseTLSPlaintext(buffer);

        if (tlsPlaintext.containsApplicationData()) {

        }

        Alert alert = null;
        if (tlsPlaintext.containsAlert()) {
            alert = factory.parser().parseAlert(tlsPlaintext.getFragment());
        } else if (tlsPlaintext.containsApplicationData()) {

        }


        output.info("received an alert: %s", alert);

        if (alert == null) {
            throw new IOException("expected an alert");
        }

        return this;
    }

    
}
