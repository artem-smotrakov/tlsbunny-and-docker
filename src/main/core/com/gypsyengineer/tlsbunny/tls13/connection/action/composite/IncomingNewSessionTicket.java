package com.gypsyengineer.tlsbunny.tls13.connection.action.composite;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.connection.action.ActionFailed;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.io.IOException;

public class IncomingNewSessionTicket extends AbstractAction {

    @Override
    public String name() {
        return "NewSessionTicket";
    }

    @Override
    public Action run() throws ActionFailed, AEADException, IOException {
        byte[] content = processEncrypted(context.applicationDataDecryptor, ContentType.handshake);
        Handshake handshake = context.factory().parser().parseHandshake(content);
        if (!handshake.containsNewSessionTicket()) {
            throw new ActionFailed("handshake message should contain NewSessionTicket");
        }

        // TODO: handle NewSessionTicket

        return this;
    }
}
