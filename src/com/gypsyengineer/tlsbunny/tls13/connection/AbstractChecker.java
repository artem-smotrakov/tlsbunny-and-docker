package com.gypsyengineer.tlsbunny.tls13.connection;

public abstract class AbstractChecker implements Check {

    TLSConnection connection;
    boolean failed = true;

    @Override
    public Check set(TLSConnection connection) {
        this.connection = connection;
        return this;
    }

    @Override
    public boolean failed() {
        return failed;
    }

}
