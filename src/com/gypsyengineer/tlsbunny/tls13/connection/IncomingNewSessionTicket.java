package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.io.IOException;

public class IncomingNewSessionTicket extends AbstractAction {

    @Override
    public String name() {
        return "NewSessionTicket";
    }

    @Override
    public Action run() throws Exception {
        byte[] content = processEncrypted(context.applicationDataDecryptor, ContentType.handshake);
        Handshake handshake = factory.parser().parseHandshake(content);
        if (!handshake.containsNewSessionTicket()) {
            throw new IOException("handshake message should contain NewSessionTicket");
        }

        // TODO: handle NewSessionTicket

        return this;
    }
}
