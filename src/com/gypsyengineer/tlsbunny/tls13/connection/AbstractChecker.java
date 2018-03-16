package com.gypsyengineer.tlsbunny.tls13.connection;

public abstract class AbstractChecker implements Check {

    @Override
    public void run() {

    }

    @Override
    public boolean failed() {
        return false;
    }

    @Override
    public String name() {
        return "fake";
    }
}
