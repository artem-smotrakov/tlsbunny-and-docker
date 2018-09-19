package com.gypsyengineer.tlsbunny.impl.test.tls13;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.utils.Output;

public class TestForServer {

    private Client client;
    private Server server;

    public TestForServer set(Client client) {
        this.client = client;
        return this;
    }

    public TestForServer set(Server server) {
        this.server = server;
        return this;
    }

    public TestForServer run() throws Exception {
        if (client == null) {
            throw new IllegalStateException("what the hell? client is not set! (null)");
        }

        if (server == null) {
            throw new IllegalStateException("what the hell? server is not set! (null)");
        }

        try (Output clientOutput = new Output("client");
             Output serverOutput = new Output("server")) {

            // start the server if it's not running
            Thread serverThread = null;
            if (!server.running()) {
                server.set(serverOutput);
                serverThread = server.start();
                Utils.waitServerStart(server);
            }

            // configure and run the client
            client.config().port(server.port());
            client.set(clientOutput);
            client.connect();

            // stop the server if we started it in the test
            if (serverThread != null) {
                server.stop();
                Utils.waitServerStop(server);
            }
        }

        return this;
    }

}
