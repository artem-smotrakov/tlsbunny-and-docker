package com.gypsyengineer.tlsbunny.tls13.connection;

public interface Check {
    String name();
    Check set(TLSConnection connection);
    Check run();
    boolean failed();
}
