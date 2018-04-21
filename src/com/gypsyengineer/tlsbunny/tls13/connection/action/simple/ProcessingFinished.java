package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;

public class ProcessingFinished extends AbstractAction {

    @Override
    public String name() {
        return "processing a Finished";
    }

    @Override
    public Action run() throws Exception {
        Finished finished = context.factory.parser().parseFinished(in, context.suite.hashLength());
        output.info("received a Finished message");

        return this;
    }

}
