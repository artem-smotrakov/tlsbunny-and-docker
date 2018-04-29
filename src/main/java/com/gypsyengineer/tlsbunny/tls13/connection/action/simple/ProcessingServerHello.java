package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.nio.ByteBuffer;

public class ProcessingServerHello extends AbstractAction {

    @Override
    public String name() {
        return "processing a ServerHello";
    }

    @Override
    public Action run() throws Exception {
        ServerHello serverHello = context.factory.parser().parseServerHello(in);
        output.info("received a ServerHello message");

        return this;
    }

}
