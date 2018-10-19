package com.gypsyengineer.tlsbunny.impl.test.tls13;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class TestForClient {

    private static final long delay = 3 * 1000; // in millis

    private Client client;
    private Server server;

    public TestForClient set(Client client) {
        this.client = client;
        return this;
    }

    public TestForClient set(Server server) {
        this.server = server;
        return this;
    }

    public TestForClient run() throws Exception {
        if (client == null) {
            throw whatTheHell("client is not set! (null)");
        }

        if (server == null) {
            throw whatTheHell("server is not set! (null)");
        }

        try (Output clientOutput = new Output("client");
             Output serverOutput = new Output("server")) {

            // start the server
            server.set(serverOutput);
            Thread serverThread = server.start();
            Utils.waitStart(server);

            // configure and run the client
            client.config().port(server.port());
            client.set(clientOutput);
            Thread clientThread = client.start();

            // wait for client or server to finish
            while (true) {
                if (!server.running()) {
                    // server stopped, stop the client and exit
                    client.stop();
                    Utils.waitStop(client);
                    break;
                }

                if (!client.running()) {
                    // client stopped, stop the server and exit
                    server.stop();
                    Utils.waitStop(server);
                    break;
                }

                Utils.sleep(delay);
            }

            if (clientThread.isAlive()) {
                throw whatTheHell("client thread is still running!");
            }

            if (serverThread.isAlive()) {
                throw whatTheHell("server thread is still running!");
            }

            checkForASanFindings(clientOutput);
        }

        return this;
    }

}
