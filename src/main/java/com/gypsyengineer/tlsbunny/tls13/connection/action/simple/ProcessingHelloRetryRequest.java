package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.HelloRetryRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;

import java.nio.ByteBuffer;

public class ProcessingHelloRetryRequest extends AbstractAction {

    @Override
    public String name() {
        return "processing a HelloRetryRequest";
    }

    @Override
    public Action run() throws Exception {
        HelloRetryRequest helloRetryRequest = context.factory.parser().parseHelloRetryRequest(in);
        out = ByteBuffer.wrap(helloRetryRequest.encoding());
        output.info("received a HelloRetryRequest message");

        return this;
    }

}
