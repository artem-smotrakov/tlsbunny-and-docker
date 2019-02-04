package com.gypsyengineer.tlsbunny.utils;

import com.gypsyengineer.tlsbunny.output.*;
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
        printToFile = Boolean.valueOf(System.getProperty(
                "tlsbunny.output.to.file", "false"));
        dirName = String.format("logs/%s",
                new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));

        if (printToFile) {
            boolean success = new File(dirName).mkdirs();
            if (!success) {
                throw whatTheHell("could not create directories");
            }
        }
    }

    private Client client;
    private Server server;
    private Output output;
    private Output consoleOutput;
    private Output fileOutput;
    private int clientIndex;
    private int serverIndex;
    private String logPrefix = "";
    private boolean initialized = false;

    @Override
    public Sync logPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
        return this;
    }

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

        output = new LocalOutput();

        consoleOutput = new ConsoleOutput(output);
        consoleOutput.prefix("");

        if (printToFile) {
            String filename = String.format("%s/%s_%d.log",
                    dirName, logPrefix, System.currentTimeMillis());
            fileOutput = new FileOutput(output, filename);
            fileOutput.prefix("");
        }

        clientIndex = client.output().lines().size();
        serverIndex = server.output().lines().size();

        initialized = true;
        return this;
    }

    @Override
    public SyncImpl start() {
        checkInitialized();
        consoleOutput.info("[sync] start");
        return this;
    }

    @Override
    public SyncImpl end() {
        checkInitialized();

        output.info("[sync] client output");
        List<Line> clientLines = client.output().lines();
        for (;clientIndex < clientLines.size(); clientIndex++) {
            output.add(clientLines.get(clientIndex));
        }

        output.info("[sync] server output");
        List<Line> serverLines = server.output().lines();
        for (;serverIndex < serverLines.size(); serverIndex++) {
            output.add(serverLines.get(serverIndex));
        }

        output.info("[sync] end");
        output.flush();

        consoleOutput.flush();
        if (printToFile) {
            fileOutput.flush();
        }

        return this;
    }

    private void checkInitialized() {
        if (!initialized) {
            throw whatTheHell("Sync is not initialized!");
        }
    }

    @Override
    public void close() {
        consoleOutput.close();
    }
}
