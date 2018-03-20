package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.ChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import java.io.IOException;
import java.nio.ByteBuffer;

public class IncomingChangeCipherSpec extends AbstractReceivingAction {

    @Override
    boolean runImpl(ByteBuffer buffer) throws IOException {
        TLSPlaintext tlsPlaintext = factory.parser().parseTLSPlaintext(buffer);
        if (!tlsPlaintext.containsChangeCipherSpec()) {
            throw new IOException("expected a change cipher spec message");
        }

        ChangeCipherSpec ccs = factory.parser().parseChangeCipherSpec(tlsPlaintext.getFragment());
        if (!ccs.isValid()) {
            throw new IOException("unexpected content in change_cipher_spec message");
        }

        return true;
    }

}
