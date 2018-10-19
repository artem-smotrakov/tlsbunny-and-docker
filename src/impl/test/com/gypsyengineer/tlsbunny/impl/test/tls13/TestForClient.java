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
            Utils.waitServerStart(server);

            // configure and run the client
            // TODO: consider adding Client.start() method
            //       which starts the client in a new thread
            client.config().port(server.port());
            client.set(clientOutput);
            Thread clientThread = new Thread(new ClientRunner(client, clientOutput));
            clientThread.start();

            // wait for client or server to finish
            while (true) {
                if (!server.running()) {
                    // server stopped, stop the client and exit
                    // TODO: implement - should we add Client.stop() method?
                }

                // TODO: check if client is not running, and if so, then stop the server and exit
                //       should we add Client.running() method?

                Utils.sleep(delay);
            }

            // TODO: check if neither client or server is still running,
            //       and throw an exception if so

            //checkForASanFindings(clientOutput);
        }

        //return this;
    }

    private static class ClientRunner implements Runnable {

        private final Client client;
        private final Output output;

        // TODO: should Client extend HasOutput?
        private ClientRunner(Client client, Output output) {
            this.client = client;
            this.output = output;
        }

        @Override
        public void run() {
            try (client) {
                client.connect();
            } catch (Exception e) {
                output.achtung("exception on client side", e);
            }
        }
    }

}
