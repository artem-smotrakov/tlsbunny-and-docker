package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.utils.Connection;

public abstract class AbstractAction implements Action {

    byte[] data;
    Connection connection;

    @Override
    public void init(byte[] data) {
        this.data = data;
    }

    @Override
    public void init(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean succeeded() {
        return false;
    }

    @Override
    public byte[] data() {
        return data.clone();
    }
}
