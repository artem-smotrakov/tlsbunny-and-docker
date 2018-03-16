package com.gypsyengineer.tlsbunny.tls13.connection;

public abstract class AbstractAction implements Action {

    @Override
    public boolean hasData() {
        return false;
    }

    @Override
    public boolean needsData() {
        return false;
    }

    @Override
    public void send() {

    }

    @Override
    public void receive() {

    }
}
