package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;

public class SkippingData extends AbstractAction {

    @Override
    public String name() {
        return "skip data";
    }

    @Override
    public Action run() {
        byte[] data = new byte[in.remaining()];
        in.get(data);
        output.info("skipped %d bytes", data.length);

        return this;
    }

}
