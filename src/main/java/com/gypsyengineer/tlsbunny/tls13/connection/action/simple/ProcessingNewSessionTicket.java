package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.NewSessionTicket;

public class ProcessingNewSessionTicket extends AbstractAction {

    @Override
    public String name() {
        return "processing NewSessionTicket";
    }

    @Override
    public Action run() {
        NewSessionTicket ticket = context.factory.parser().parseNewSessionTicket(in);
        output.info("received a NewSessionTicket message");

        return this;
    }
}
