package com.gypsyengineer.tlsbunny.tls13.connection;

// the check fails if an alert with a error received
public class NoAlertCheck extends AbstractCheck {

    @Override
    public Check run() {
        failed = context.hasAlert() && !context.getAlert().isWarning();
        return this;
    }

    @Override
    public String name() {
        return "no fatal alert received";
    }

}
