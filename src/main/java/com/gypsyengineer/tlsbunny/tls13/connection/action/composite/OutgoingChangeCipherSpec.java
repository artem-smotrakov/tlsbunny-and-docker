package com.gypsyengineer.tlsbunny.tls13.connection.action.composite;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.tls13.utils.TLS13Utils;

import java.io.IOException;

public class OutgoingChangeCipherSpec extends AbstractAction {

    @Override
    public String name() {
        return "generating ChangeCipherSpec";
    }

    @Override
    public Action run() throws IOException {
        ChangeCipherSpec ccs = context.factory.createChangeCipherSpec(ChangeCipherSpec.VALID_VALUE);
        TLSPlaintext[] tlsPlaintexts = context.factory.createTLSPlaintexts(
                ContentType.change_cipher_spec,
                ProtocolVersion.TLSv12,
                ccs.encoding());

        out = TLS13Utils.store(tlsPlaintexts);

        return this;
    }

}
