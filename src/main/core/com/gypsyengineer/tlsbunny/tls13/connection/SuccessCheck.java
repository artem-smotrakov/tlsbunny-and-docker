package com.gypsyengineer.tlsbunny.tls13.connection;

public class SuccessCheck extends AbstractCheck {

    @Override
    public Check run() {
        failed = engine.status() != Engine.Status.success;
        return this;
    }

    @Override
    public String name() {
        return "connection succeeded";
    }

}
