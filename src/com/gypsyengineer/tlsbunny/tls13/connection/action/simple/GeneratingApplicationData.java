package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;

import java.nio.ByteBuffer;

public class GeneratingApplicationData extends AbstractAction {

    private byte[] data;

    @Override
    public String name() {
        return "generating application data";
    }

    public GeneratingApplicationData data(String string) {
        data = string.getBytes();
        return this;
    }

    @Override
    public Action run() throws Exception {
        out = ByteBuffer.wrap(data);

        return this;
    }

}
