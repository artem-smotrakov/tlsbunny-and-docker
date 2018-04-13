package com.gypsyengineer.tlsbunny.tls13.connection.action.composite;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.io.IOException;

public class IncomingAlert extends AbstractAction {

    @Override
    public String name() {
        return "Alert";
    }

    @Override
    public Action run() throws Exception {
        TLSPlaintext tlsPlaintext = factory.parser().parseTLSPlaintext(in);

        Alert alert;
        if (tlsPlaintext.containsAlert()) {
            alert = factory.parser().parseAlert(tlsPlaintext.getFragment());
        } else if (tlsPlaintext.containsApplicationData()) {
            TLSInnerPlaintext tlsInnerPlaintext = factory.parser().parseTLSInnerPlaintext(
                    context.applicationDataDecryptor.decrypt(tlsPlaintext));

            if (!tlsInnerPlaintext.containsAlert()) {
                throw new IOException("expected an alert");
            }

            alert = factory.parser().parseAlert(tlsInnerPlaintext.getContent());
        } else {
            throw new IOException("expected an alert");
        }

        if (alert != null) {
            context.setAlert(alert);
        }

        output.info("received an alert: %s", alert);

        return this;
    }

    
}
