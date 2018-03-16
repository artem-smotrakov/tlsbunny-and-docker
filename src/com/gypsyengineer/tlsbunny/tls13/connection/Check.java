package com.gypsyengineer.tlsbunny.tls13.connection;

public interface Check {
    String name();
    void run();
    boolean failed();
}
