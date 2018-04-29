package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;

import java.nio.ByteBuffer;

public class PreservingEncryptedApplicationData extends AbstractAction {

    @Override
    public String name() {
        return "preserving encrypted application data";
    }

    @Override
    public Action run() throws Exception {
        byte[] data = new byte[in.remaining()];
        in.get(data);
        applicationDataOut = ByteBuffer.wrap(data);
        output.info("preserved %d bytes of encrypted application out", data.length);

        return this;
    }

}
