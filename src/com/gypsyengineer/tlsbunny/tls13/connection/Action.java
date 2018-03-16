package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.utils.Connection;

public interface Action {
    void init(byte[] data);
    void init(Connection connection);
    void run();
    boolean succeeded();
    byte[] data();
}
