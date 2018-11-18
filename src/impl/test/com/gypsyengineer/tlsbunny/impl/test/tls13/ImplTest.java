package com.gypsyengineer.tlsbunny.impl.test.tls13;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.UncaughtExceptionHandler;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.sleep;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class ImplTest {

    // in millis
    private static final long delay = 3 * 1000;

    private Client client;
    private Server server;

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

        // it may be better to set a separate exception handler for client and server
        // threads, but it doesn't work for some reason
        // TODO look into it
        Thread.UncaughtExceptionHandler previousExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        UncaughtExceptionHandler exceptionHandler = new UncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

        Thread serverThread = null;
        Thread clientThread;
        try (Output clientOutput = new Output("client");
             Output serverOutput = new Output("server")) {

            // start the server if it's not running
            if (!server.running()) {
                server.set(serverOutput);
                serverThread = server.start();
                Utils.waitStart(server);
            }

            // configure and run the client
            if (client.running()) {
                throw whatTheHell("client is already running!");
            }

            client.config().port(server.port());
            client.set(clientOutput);
            clientThread = client.start();
            sleep(delay);

            // wait for client or server to finish
            while (true) {
                if (!server.running()) {
                    // server stopped

                    // stop the client if we started it in this test
                    client.stop();
                    Utils.waitStop(client);

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

            if (clientThread.isAlive()) {
                throw whatTheHell("client thread is still running!");
            }

            if (serverThread != null && serverThread.isAlive()) {
                throw whatTheHell("server thread is still running!");
            }

            if (exceptionHandler.knowsSomething()) {
                throw whatTheHell("unexpected exception", exceptionHandler.exception());
            }
        } finally {
            // restore exception handler
            Thread.setDefaultUncaughtExceptionHandler(previousExceptionHandler);

            if (serverThread != null && server.running()) {
                server.stop();
                Utils.waitStop(server);
            }

            if (client.running()) {
                client.stop();
                Utils.waitStop(client);
            }

            checkForASanFindings(client.output());
            checkForASanFindings(server.output());
        }

        return this;
    }

}
