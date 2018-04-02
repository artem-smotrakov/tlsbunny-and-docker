package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import java.io.IOException;

public class IncomingEncryptedExtensions extends AbstractAction {

    @Override
    public String name() {
        return "EncryptedExtensions";
    }

    @Override
    public Action run() throws Exception {
        Handshake handshake = processEncryptedHandshake();
        if (!handshake.containsEncryptedExtensions()) {
            throw new IOException("expected a EncryptedExtensions message");
        }

        processEncryptedExtensions(handshake);

        return this;
    }

    private void processEncryptedExtensions(Handshake handshake) {
        factory.parser().parseEncryptedExtensions(handshake.getBody());
        context.setEncryptedExtensions(handshake);
    }
}
