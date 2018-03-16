package com.gypsyengineer.tlsbunny.tls13.connection;

public interface Action {

    boolean hasData();
    boolean needsData();
    void send();
    void receive();
}
