package com.gypsyengineer.tlsbunny.utils;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class SyncImpl implements Sync {

    private static final boolean printToFile;
    private static final String dirName;
    static {
        printToFile = Boolean.valueOf(System.getProperty("tlsbunny.output.to.file", "false"));
        dirName = String.format("logs/%s",
                new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));
        boolean success = new File(dirName).mkdirs();
        if (!success) {
            throw whatTheHell("could not create directories");
        }
    }

    private Client client;
    private Server server;
    private Output output;
    private int clientIndex;
    private int serverIndex;
    private boolean initialized = false;

    @Override
    public SyncImpl set(Client client) {
        this.client = client;
        initialized = false;
        return this;
    }

    @Override
    public SyncImpl set(Server server) {
        this.server = server;
        initialized = false;
        return this;
    }

    @Override
    public SyncImpl init() {
        Objects.requireNonNull(client, "client can't be null!");
        Objects.requireNonNull(server, "server can't be null!");
        output = new ConsoleOutput(new OutputStorage());
        output.prefix("");
        clientIndex = 0;
        serverIndex = 0;
        initialized = true;
        return this;
    }

    @Override
    public SyncImpl start() {
        checkInitialized();
        output.info("[sync] start");
        return this;
    }

    @Override
    public SyncImpl end() {
        checkInitialized();

        output.info("[sync] client output");
        List<String> clientLines = client.output().lines();
        for (;clientIndex < clientLines.size(); clientIndex++) {
            output.info(clientLines.get(clientIndex));
        }

        output.info("[sync] server output");
        List<String> serverLines = server.output().lines();
        for (;serverIndex < serverLines.size(); serverIndex++) {
            output.info(serverLines.get(serverIndex));
        }

        output.info("[sync] end");
        output.flush();

        return this;
    }

    private void checkInitialized() {
        if (!initialized) {
            throw whatTheHell("Sync is not initialized!");
        }
    }

    private Writer createFileWriter(String fileName) {
        if (printToFile) {
            try {
                return new BufferedWriter(new FileWriter(fileName));
            } catch (IOException e) {
                throw whatTheHell("unexpected exception", e);
            }
        }

        return null;
    }

    @Override
    public void close() {
        output.close();
    }
}