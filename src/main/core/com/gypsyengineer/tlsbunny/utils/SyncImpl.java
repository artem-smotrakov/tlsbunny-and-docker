package com.gypsyengineer.tlsbunny.utils;

import com.gypsyengineer.tlsbunny.output.*;
import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.gypsyengineer.tlsbunny.output.Level.important;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class SyncImpl implements Sync {

    private static final long n = 100;
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
    private SyncStandardOutput consoleOutput;
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

        consoleOutput = new SyncStandardOutput();
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
            consoleOutput.important("oops!");
            consoleOutput.important("Looks like AddressSanitizer found something");
            consoleOutput.important("[sync] client output");
            for (int i = oldClientIndex; i < clientIndex; i++) {
                consoleOutput.add(clientLines.get(i));
            }
            consoleOutput.important("[sync] server output");
            for (int i = oldServerIndex; i < serverIndex; i++) {
                consoleOutput.add(serverLines.get(i));
            }
            consoleOutput.flush();
        } else {
            if (++tests % n == 0) {
                long speed = n * 60000000000L / testsDuration;
                consoleOutput.important("%d tests done, %d tests / minute",
                        tests, speed);
                testsDuration = 0;
                consoleOutput.flush();
            }
        }

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

    private static class NoNewLine extends Line {

        NoNewLine(Level level, String value) {
            super(level, value);
        }
    }

    private static class SyncStandardOutput extends StandardOutput {

        public SyncStandardOutput dot() {
            add(new NoNewLine(important, "."));
            return this;
        }

        @Override
        public void flush() {
            synchronized (consoleLock) {
                output.flush();

                List<Line> lines = output.lines();
                for (;index < lines.size(); index++) {
                    Line line = lines.get(index);

                    if (!line.printable(level)) {
                        continue;
                    }

                    String string = line.value();
                    if (line instanceof NoNewLine) {
                        System.out.print(string);
                    } else {
                        System.out.println(string);
                    }

                    System.out.flush();
                }
            }
        }
    }
}
