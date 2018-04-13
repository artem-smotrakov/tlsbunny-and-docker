package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.Alert;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

import java.io.IOException;

public class TLSPlaintextWithAlert extends AbstractAction {

    @Override
    public String name() {
        return "TLSPlaintext with alert";
    }

    @Override
    public Action run() throws Exception {
        TLSPlaintext tlsPlaintext = factory.parser().parseTLSPlaintext(in);

        if (!tlsPlaintext.containsAlert()) {
            throw new IOException("expected an alert");
        }

        Alert alert = factory.parser().parseAlert(tlsPlaintext.getFragment());
        context.setAlert(alert);

        output.info("received an alert: %s", alert);

        return this;
    }

    
}
