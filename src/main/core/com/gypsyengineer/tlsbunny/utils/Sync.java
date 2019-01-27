package com.gypsyengineer.tlsbunny.utils;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;

public interface Sync extends AutoCloseable {

    static Sync dummy() {
        return new DummySync();
    }

    static Sync between(Client client, Server server) {
        return new SyncImpl()
                .set(client)
                .set(server)
                .init();
    }

    Sync set(Client client);
    Sync set(Server server);
    Sync init();
    Sync start();
    Sync end();
}
