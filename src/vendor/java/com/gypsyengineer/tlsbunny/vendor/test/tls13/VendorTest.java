package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.output.Output;
import com.gypsyengineer.tlsbunny.utils.Sync;
import com.gypsyengineer.tlsbunny.utils.UncaughtExceptionHandler;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils.sleep;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class VendorTest {

    // in millis
    private static final long delay = 3 * 1000;

    private Client client;
    private Server server;

    private String label = "";

    public VendorTest label(String label) {
        this.label = label;
        return this;
    }

    public VendorTest set(Client client) {
        this.client = client;
        return this;
    }

    public VendorTest set(Server server) {
        this.server = server;
        return this;
    }

    public VendorTest run() throws Exception {
        check();

        // it may be better to set a separate exception handler for client and server
        // threads, but it doesn't work for some reason
        // TODO look into it
        Thread.UncaughtExceptionHandler previousExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        UncaughtExceptionHandler exceptionHandler = new UncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

        configureOutputs();

        Sync sync = Sync.between(client, server);
        sync.logPrefix(prefix());
        sync.init();

        Thread serverThread = startServerIfNecessary();
        Thread clientThread = startClientIfNecessary();

        try {
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

            if (clientThread != null && clientThread.isAlive()) {
                throw whatTheHell("client thread is still running!");
            }

            if (serverThread != null && serverThread.isAlive()) {
                throw whatTheHell("server thread is still running!");
            }

            if (exceptionHandler.knowsSomething()) {
                throw whatTheHell("unexpected exception",
                        exceptionHandler.exception());
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

            sync.close();

            checkForASanFindings(client.output());
            checkForASanFindings(server.output());
        }

        return this;
    }

    private void check() {
        if (client == null) {
            throw whatTheHell("client is not set! (null)");
        }

        if (server == null) {
            throw whatTheHell("server is not set! (null)");
        }
    }

    private void configureOutputs() {
        Output clientOutput;
        Output serverOutput;

        // set up an output for the server if necessary
        if (!server.running() && server.output() == null) {
            serverOutput = Output.local("server");
            server.set(serverOutput);
        }

        // set up an output for the client if necessary
        if (!client.running() && client.output() == null) {
            clientOutput = Output.local("client");
            client.set(clientOutput);
        }
    }

    private String prefix() {
        if (label != null && !label.isEmpty()) {
            return label;
        }

        return String.format("%s_%s",
                client.getClass().getSimpleName(),
                server.getClass().getSimpleName());
    }

    private Thread startServerIfNecessary() throws IOException, InterruptedException {
        // start the server if it's not running
        if (!server.running()) {
            Thread serverThread = server.start();
            Utils.waitStart(server);
            return serverThread;
        }

        return null;
    }

    private Thread startClientIfNecessary() {
        // configure and run the client
        if (!client.running()) {
            client.config().port(server.port());
            Thread clientThread = client.start();
            sleep(delay);
            return clientThread;
        }

        return null;
    }

}
