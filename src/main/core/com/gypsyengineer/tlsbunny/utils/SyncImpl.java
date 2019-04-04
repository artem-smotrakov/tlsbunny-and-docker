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

    private static final int n = 100;

    private static Level standardOutputLevel = Level.valueOf(
            System.getProperty("tlsbunny.sync.output.standard.level",
                    Level.info.name()));

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
    private StandardOutput standardOutput;
    private Output fileOutput;
    private int clientIndex;
    private int serverIndex;
    private String logPrefix = "";
    private boolean initialized = false;
    private long tests = 0;
    private long testStarted;
    private long testsDuration = 0;

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

        standardOutput = Output.standard();
        standardOutput.set(standardOutputLevel);
        standardOutput.prefix("");

        if (printToFile) {
            String path = String.format("%s/%s_%d.log",
                    dirName, logPrefix, System.currentTimeMillis());
            fileOutput = Output.file(path);
            fileOutput.prefix("");
        }

        try (Output output = Output.local()) {
            output.info("[sync] init");

            output.info("[sync] client output");
            List<Line> clientLines = client.output().lines();
            for (; clientIndex < clientLines.size(); clientIndex++) {
                output.add(clientLines.get(clientIndex));
            }

            output.info("[sync] server output");
            List<Line> serverLines = server.output().lines();
            for (; serverIndex < serverLines.size(); serverIndex++) {
                output.add(serverLines.get(serverIndex));
            }

            standardOutput.add(output);
            standardOutput.flush();
        }

        clientIndex = client.output().lines().size();
        serverIndex = server.output().lines().size();

        initialized = true;
        return this;
    }

    @Override
    public SyncImpl start() {
        checkInitialized();
        testStarted = System.nanoTime();
        return this;
    }

    @Override
    public SyncImpl end() {
        checkInitialized();

        long time = System.nanoTime() - testStarted;
        testsDuration += time;

        try (Output output = new LocalOutput()) {
            output.info("[sync] client output");
            List<Line> clientLines = client.output().lines();
            for (; clientIndex < clientLines.size(); clientIndex++) {
                output.add(clientLines.get(clientIndex));
            }

            output.info("[sync] server output");
            List<Line> serverLines = server.output().lines();
            boolean found = false;
            for (; serverIndex < serverLines.size(); serverIndex++) {
                Line line = serverLines.get(serverIndex);
                if (line.value().contains("ERROR: AddressSanitizer:")) {
                    found = true;
                }
                output.add(line);
            }

            output.info("[sync] end");
            output.flush();

            if (found) {
                standardOutput.achtung("oops!");
                standardOutput.achtung("Looks like AddressSanitizer found something");
                standardOutput.add(output);

                if (printToFile) {
                    String path = String.format("%s/oops_%s_%d.log",
                            dirName, logPrefix, System.currentTimeMillis());
                    try (Output oopsOutput = Output.file(path)) {
                        oopsOutput.add(output);
                    }
                }
            } else {
                if (++tests % n == 0) {
                    long speed = n * 60000000000L / testsDuration;
                    standardOutput.important("%d tests done, %d tests / minute",
                            tests, speed);
                    testsDuration = 0;
                }
            }

            standardOutput.flush();
            standardOutput.clear();

            if (printToFile) {
                fileOutput.add(output);
                fileOutput.flush();
                fileOutput.clear();
            }
        }

        client.output().clear();
        server.output().clear();

        clientIndex = 0;
        serverIndex = 0;

        return this;
    }

    @Override
    public Output output() {
        return standardOutput;
    }

    private void checkInitialized() {
        if (!initialized) {
            throw whatTheHell("Sync is not initialized!");
        }
    }

    @Override
    public void close() {
        standardOutput.close();
        if (printToFile) {
            fileOutput.close();
        }
    }

}
