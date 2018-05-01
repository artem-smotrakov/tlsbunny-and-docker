package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;

public class PrintingData extends AbstractAction {

    @Override
    public String name() {
        return "printing data";
    }

    @Override
    public Action run() {
        byte[] data = new byte[in.remaining()];
        in.get(data);
        output.info("received application data:%n%s", new String(data));

        return this;
    }

}
