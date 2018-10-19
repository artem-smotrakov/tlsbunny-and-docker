package com.gypsyengineer.tlsbunny.impl.test.tls13;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class ImplTest {

    private static final long delay = 3 * 1000; // in millis

    private Client client;
    private Server server;

    private Thread serverThread;
    private Thread clientThread;

    public ImplTest set(Client client) {
        this.client = client;
        return this;
    }

    public ImplTest set(Server server) {
        this.server = server;
        return this;
    }

    public ImplTest run() throws Exception {
        if (client == null) {
            throw whatTheHell("client is not set! (null)");
        }

        if (server == null) {
            throw whatTheHell("server is not set! (null)");
        }

        try (Output clientOutput = new Output("client");
             Output serverOutput = new Output("server")) {

            // start the server if it's not running
            if (!server.running()) {
                server.set(serverOutput);
                serverThread = server.start();
                Utils.waitStart(server);
            }

            // configure and run the client if it's not running
            if (!client.running()) {
                client.config().port(server.port());
                client.set(clientOutput);
                clientThread = client.start();
                Utils.waitStart(client);
            }

            // wait for client or server to finish
            while (true) {
                if (!server.running()) {
                    // server stopped

                    // stop the client if we started it in this test
                    if (clientThread != null) {
                        client.stop();
                        Utils.waitStop(client);
                    }

                    // and exit
                    break;
                }

                if (!client.running()) {
                    // client stopped

                    // stop the server if we started in this test
                    if (serverThread != null) {
                        server.stop();
                        Utils.waitStop(server);
                    }

                    // and exit
                    break;
                }

                Utils.sleep(delay);
            }

            if (clientThread != null && clientThread.isAlive()) {
                throw whatTheHell("client thread is still running!");
            }

            if (serverThread != null && serverThread.isAlive()) {
                throw whatTheHell("server thread is still running!");
            }

            checkForASanFindings(client.output());
            checkForASanFindings(server.output());
        }

        return this;
    }

}
