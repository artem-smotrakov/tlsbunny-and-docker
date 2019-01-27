package com.gypsyengineer.tlsbunny.utils;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;

public class DummySync implements Sync {

    @Override
    public Sync set(Client client) {
        return this;
    }

    @Override
    public Sync set(Server server) {
        return this;
    }

    @Override
    public Sync init() {
        return this;
    }

    @Override
    public Sync start() {
        return this;
    }

    @Override
    public Sync end() {
        return this;
    }

    @Override
    public void close() {
        // do nothing
    }
}