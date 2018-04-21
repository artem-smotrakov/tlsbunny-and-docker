package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;

public class PrintingData extends AbstractAction {

    @Override
    public String name() {
        return "printing data";
    }

    @Override
    public Action run() throws Exception {
        byte[] data = new byte[in.remaining()];
        in.get(data);
        output.info(new String(data));

        return this;
    }

}
