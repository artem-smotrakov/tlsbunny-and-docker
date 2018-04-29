package com.gypsyengineer.tlsbunny.tls13.connection;

public class NoAlertCheck extends AbstractCheck {

    @Override
    public Check run() {
        failed = context.hasAlert();
        return this;
    }

    @Override
    public String name() {
        return "no alert received";
    }

}
