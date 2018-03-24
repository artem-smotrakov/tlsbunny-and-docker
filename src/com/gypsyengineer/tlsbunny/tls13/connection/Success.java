package com.gypsyengineer.tlsbunny.tls13.connection;

public class Success extends AbstractChecker {

    @Override
    public Check run() {
        failed = connection.status() != TLSConnection.Status.success;
        return this;
    }

    @Override
    public String name() {
        return "successful connection";
    }

}
