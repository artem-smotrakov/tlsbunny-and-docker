package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;

import java.nio.ByteBuffer;

public class RestoringEncryptedApplicationData extends AbstractAction {

    @Override
    public String name() {
        return "restoring encrypted application data";
    }

    @Override
    public Action run() throws Exception {
        byte[] data = new byte[applicationDataIn.remaining()];
        applicationDataIn.get(data);
        out = ByteBuffer.wrap(data);
        output.info("restored %d bytes of encrypted application out", data.length);

        return this;
    }

}
