package com.gypsyengineer.tlsbunny.tls13.connection;

public abstract class AbstractChecker implements Check {

    Engine connection;
    boolean failed = true;

    @Override
    public Check set(Engine connection) {
        this.connection = connection;
        return this;
    }

    @Override
    public boolean failed() {
        return failed;
    }

}
