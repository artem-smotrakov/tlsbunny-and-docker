package com.gypsyengineer.tlsbunny.tls13.action;

public interface Action {

    boolean hasData();
    boolean needsData();
    void send();
    void receive();
}
