package com.gypsyengineer.tlsbunny.tls13.connection;

public class FailureCheck extends AbstractCheck {

    @Override
    public Check run() {
        failed = engine.status() == Engine.Status.success;
        return this;
    }

    @Override
    public String name() {
        return "connection failed";
    }

}
