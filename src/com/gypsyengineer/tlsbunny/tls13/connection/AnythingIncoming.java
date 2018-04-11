package com.gypsyengineer.tlsbunny.tls13.connection;

public class AnythingIncoming extends AbstractAction {

    @Override
    public String name() {
        return "anything";
    }

    @Override
    public Action run() {
        int read = buffer.remaining();
        buffer.position(buffer.limit());
        output.info("received %d bytes", read);

        return this;
    }

    
}
