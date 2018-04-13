package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;

public class AnythingIncoming extends AbstractAction {

    @Override
    public String name() {
        return "anything";
    }

    @Override
    public Action run() {
        int read = in.remaining();
        in.position(in.limit());
        output.info("received %d bytes", read);

        return this;
    }

    
}
