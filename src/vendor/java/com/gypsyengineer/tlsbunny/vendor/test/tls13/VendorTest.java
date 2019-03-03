package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.output.Output;
import com.gypsyengineer.tlsbunny.utils.Sync;
import com.gypsyengineer.tlsbunny.utils.UncaughtExceptionHandlerImpl;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class VendorTest {

    // in millis
    private static final long delay = 3 * 1000;

    private Client client;
    private Server server;

    private Thread serverThread;
    private Thread clientThread;

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
        Thread.UncaughtExceptionHandler previousExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        UncaughtExceptionHandlerImpl exceptionHandler = new UncaughtExceptionHandlerImpl();
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

        configureOutputs();

        Sync sync = Sync.between(client, server);
        try (sync) {
            sync.logPrefix(prefix());
            sync.init();

            startServerIfNecessary(sync);
            startClientIfNecessary(sync);

            // wait for client or server to finish
            while (!server.done() && !client.done()) {
                Utils.sleep(delay);
            }

            if (exceptionHandler.knowsSomething()) {
                Throwable e = exceptionHandler.exception();
                sync.output().achtung("exception handler caught an unexpected exception", e);
                throw whatTheHell("unexpected exception", e);
            }
        } finally {
            // restore exception handler
            Thread.setDefaultUncaughtExceptionHandler(previousExceptionHandler);

            if (client.running()) {
                sync.output().important("stop the client since it's still running");
                client.stop();
                Utils.waitStop(client);
                Utils.waitStop(clientThread);
            }

            if (serverThread != null && server.running()) {
                sync.output().important("stop the server since we started it in this test");
                server.stop();
                Utils.waitStop(server);
                Utils.waitStop(serverThread);
            }

            // just in case
            checkThreads();
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

    private void startServerIfNecessary(Sync sync) throws IOException, InterruptedException {
        // start the server if it's not running
        if (!server.running()) {
            sync.output().important("start a server in a separate thread");
            serverThread = server.start();
            Utils.waitStart(server);
        }
    }

    private void startClientIfNecessary(Sync sync) throws IOException, InterruptedException {
        // configure and run the client
        if (!client.running()) {
            sync.output().important("start a client in a separate thread");
            client.config().port(server.port());
            clientThread = client.start();
            Utils.waitStart(client);
        }
    }

    private void checkThreads() {
        if (clientThread != null && clientThread.isAlive()) {
            throw whatTheHell("client thread is still running!");
        }

        if (serverThread != null && serverThread.isAlive()) {
            throw whatTheHell("server thread is still running!");
        }
    }

}
