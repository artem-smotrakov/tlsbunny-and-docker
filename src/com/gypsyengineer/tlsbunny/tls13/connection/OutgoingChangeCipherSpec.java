package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.tls13.utils.Helper;

public class OutgoingChangeCipherSpec extends AbstractAction {

    @Override
    public String name() {
        return "ChangeCipherSpec";
    }

    @Override
    public Action run() throws Exception {
        ChangeCipherSpec ccs = factory.createChangeCipherSpec(ChangeCipherSpec.VALID_VALUE);
        TLSPlaintext[] tlsPlaintexts = factory.createTLSPlaintexts(
                ContentType.change_cipher_spec,
                ProtocolVersion.TLSv12,
                ccs.encoding());

        buffer = Helper.store(tlsPlaintexts);

        return this;
    }

}
