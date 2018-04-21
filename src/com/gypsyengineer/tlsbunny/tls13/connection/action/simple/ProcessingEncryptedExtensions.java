package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.EncryptedExtensions;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;

import java.nio.ByteBuffer;

public class ProcessingEncryptedExtensions extends AbstractAction {

    @Override
    public String name() {
        return "processing an EncryptedExtensions";
    }

    @Override
    public Action run() throws Exception {
        EncryptedExtensions extensions = context.factory.parser().parseEncryptedExtensions(in);
        out = ByteBuffer.wrap(extensions.encoding());
        output.info("received an EncryptedExtensions message");

        return this;
    }

}
