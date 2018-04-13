package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ProcessingTLSPlaintext extends AbstractAction {

    public static final ContentType NO_TYPE_SPECIFIED = null;

    private ContentType expectedType = NO_TYPE_SPECIFIED;

    public ProcessingTLSPlaintext expect(ContentType type) {
        expectedType = type;
        return this;
    }

    @Override
    public String name() {
        return "TLSPlaintext";
    }

    @Override
    public Action run() throws Exception {
        TLSPlaintext tlsPlaintext = factory.parser().parseTLSPlaintext(in);

        ContentType type = tlsPlaintext.getType();
        if (expectedType != NO_TYPE_SPECIFIED && !expectedType.equals(type)) {
            throw new IOException(
                    String.format("expected %s, but found %", expectedType, type));
        }

        out = ByteBuffer.wrap(tlsPlaintext.getFragment());
        output.info("received a TLSPlaintext");

        return this;
    }

}
