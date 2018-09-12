package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;

public class ProcessingClientHello extends AbstractAction<ProcessingClientHello> {

    @Override
    public String name() {
        return "processing a ClientHello";
    }

    @Override
    public ProcessingClientHello run() {
        context.factory.parser().parseClientHello(in);
        output.info("received a ClientHello message");

        return this;
    }

}
