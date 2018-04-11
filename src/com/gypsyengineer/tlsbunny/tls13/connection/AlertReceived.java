package com.gypsyengineer.tlsbunny.tls13.connection;

public class AlertReceived extends AbstractCheck {

    @Override
    public Check run() {
        failed = !context.hasAlert();
        return this;
    }

    @Override
    public String name() {
        return "alert received";
    }

}
