package com.gypsyengineer.tlsbunny.tls13.connection.action.composite;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.ChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import java.io.IOException;

public class IncomingChangeCipherSpec extends AbstractAction {

    @Override
    public String name() {
        return "ChangeCipherSpec";
    }

    @Override
    public Action run() throws IOException {
        TLSPlaintext tlsPlaintext = context.factory.parser().parseTLSPlaintext(in);
        if (!tlsPlaintext.containsChangeCipherSpec()) {
            throw new IOException("expected a change cipher spec message");
        }

        ChangeCipherSpec ccs = context.factory.parser().parseChangeCipherSpec(tlsPlaintext.getFragment());
        if (!ccs.isValid()) {
            throw new IOException("unexpected content in change_cipher_spec message");
        }

        return this;
    }

}
