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

    private static final int maxLines = 10000;
    private static final int n = 100;
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

        output = new LocalOutput();

        standardOutput = new StandardOutput();
        standardOutput.prefix("");

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
        testStarted = System.nanoTime();
        return this;
    }

    @Override
    public SyncImpl end() {
        checkInitialized();

        long time = System.nanoTime() - testStarted;
        testsDuration += time;

        output.important("[sync] client output");
        List<Line> clientLines = client.output().lines();
        int oldClientIndex = clientIndex;
        for (; clientIndex < clientLines.size(); clientIndex++) {
            output.add(clientLines.get(clientIndex));
        }

        output.important("[sync] server output");
        List<Line> serverLines = server.output().lines();
        int oldServerIndex = serverIndex;
        boolean found = false;
        for (; serverIndex < serverLines.size(); serverIndex++) {
            Line line = serverLines.get(serverIndex);
            if (line.value().contains("ERROR: AddressSanitizer:")) {
                found = true;
            }
            output.add(line);
        }

        output.important("[sync] end");
        output.flush();

        if (found) {
            standardOutput.important("oops!");
            standardOutput.important("Looks like AddressSanitizer found something");
            standardOutput.important("[sync] client output");
            for (int i = oldClientIndex; i < clientIndex; i++) {
                standardOutput.add(clientLines.get(i));
            }
            standardOutput.important("[sync] server output");
            for (int i = oldServerIndex; i < serverIndex; i++) {
                standardOutput.add(serverLines.get(i));
            }
            standardOutput.flush();
        } else {
            if (++tests % n == 0) {
                long speed = n * 60000000000L / testsDuration;
                standardOutput.important("%d tests done, %d tests / minute",
                        tests, speed);
                testsDuration = 0;
                standardOutput.flush();
            }
        }

        if (printToFile) {
            fileOutput.flush();
        }

        if (clientIndex > maxLines) {
            client.output().clear();
            clientIndex = 0;
        }

        if (serverIndex > maxLines) {
            server.output().clear();
            serverIndex = 0;
        }

        if (standardOutput.lines().size() > maxLines) {
            standardOutput.clear();
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
        standardOutput.close();
    }

}
