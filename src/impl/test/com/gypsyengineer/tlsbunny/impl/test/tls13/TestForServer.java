package com.gypsyengineer.tlsbunny.impl.test.tls13;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;

public class TestForServer {

    private static final int delay = 500; // in millis
    private static final int server_start_timeout = 10 * 1000; // im millis
    private static final int server_stop_timeout  = 10 * 1000; // im millis

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

        // start the server if it's not running
        Thread serverThread = null;
        if (!server.running()) {
            serverThread = server.start();

            long start = System.currentTimeMillis();
            do {
                Thread.sleep(delay);
                if (System.currentTimeMillis() - start > server_start_timeout) {
                    throw new RuntimeException(
                            "timeout reached while waiting for the server to start");
                }
            } while (!server.running());
        }

        // configure and run the client
        client.config().port(server.port());
        client.connect();

        // stop the server if we started it in the test
        if (serverThread != null) {
            server.stop();

            long start = System.currentTimeMillis();
            do {
                Thread.sleep(delay);
                if (System.currentTimeMillis() - start > server_stop_timeout) {
                    throw new RuntimeException(
                            "timeout reached while waiting for the server to stop");
                }
            } while (server.running());
        }

        return this;
    }

}
