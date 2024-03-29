package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.output.Line;
import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.output.Output;

import java.io.IOException;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.Achtung.achtung;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class Utils {

    // in millis
    private static final int delay = 500;
    private static final int start_delay = 500;
    private static final int stop_delay  = 5 * 1000;
    private static final int default_timeout = 60 * 1000; // 1 minute

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw whatTheHell("could not sleep well!", e);
        }
    }

    public static Process exec(Output output, String template, Object... params)
            throws IOException {

        String command = String.format(template, params);
        output.important("run: %s", command);

        ProcessBuilder pb = new ProcessBuilder(
                command.split("\\s+"));
        pb.redirectErrorStream(true);

        return pb.start();
    }

    public static Process exec(Output output, List<String> command) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String part : command) {
            sb.append(part).append(" ");
        }
        output.important("run: %s", sb.toString());

        return exec(command);
    }

    public static Process exec(List<String> command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        return pb.start();
    }

    public static void waitStop(Thread thread)
            throws InterruptedException, IOException {

        if (thread == null || !thread.isAlive()) {
            return;
        }
        thread.join(default_timeout);
        if (thread.isAlive()) {
            throw new IOException(
                    "timeout reached while waiting for the thread to stop");
        }
    }

    public static void waitStart(Client client)
            throws IOException, InterruptedException {

        waitStart(client, default_timeout);
    }

    public static void waitStart(Client client, long timeout)
            throws IOException, InterruptedException {

        if (client.done()) {
            return;
        }

        long start = System.currentTimeMillis();
        while (client.status() == Client.Status.not_started) {
            Thread.sleep(start_delay);
            if (timeout > 0 && System.currentTimeMillis() - start > timeout) {
                throw new IOException(
                        "timeout reached while waiting for the client to start");
            }
        }
    }

    public static void waitStart(Server server)
            throws IOException, InterruptedException {

        waitStart(server, default_timeout);
    }

    public static void waitStart(Server server, long timeout)
            throws IOException, InterruptedException {

        long start = System.currentTimeMillis();
        while (server.status() == Server.Status.not_started) {
            Thread.sleep(delay);
            if (timeout > 0 && System.currentTimeMillis() - start > timeout) {
                throw new IOException(
                        "timeout reached while waiting for the server to start");
            }
        }
    }

    public static void waitStop(Client client)
            throws IOException, InterruptedException {

        waitStop(client, default_timeout);
    }

    public static void waitStop(Client client, long timeout)
            throws InterruptedException, IOException {

        long start = System.currentTimeMillis();
        while (!client.done()) {
            Thread.sleep(stop_delay);
            if (timeout > 0 && System.currentTimeMillis() - start > timeout) {
                throw new IOException(
                        "timeout reached while waiting for the client to stop");
            }
        }
    }

    public static void waitStop(Server server)
            throws IOException, InterruptedException {

        waitStop(server, default_timeout);
    }

    public static void waitStop(Server server, long timeout)
            throws InterruptedException, IOException {

        long start = System.currentTimeMillis();
        while (!server.done()) {
            Thread.sleep(delay);
            if (timeout > 0 && System.currentTimeMillis() - start > timeout) {
                throw new IOException(
                        "timeout reached while waiting for the server to stop");
            }
        };
    }

    public static void waitServerReady(Server server)
            throws IOException, InterruptedException {

        waitServerReady(server, default_timeout);
    }

    public static void waitServerReady(Server server, long timeout)
            throws IOException, InterruptedException {

        if (server.done()) {
            return;
        }

        long start = System.currentTimeMillis();
        while (!server.ready()) {
            Thread.sleep(delay);
            if (timeout > 0 && System.currentTimeMillis() - start > timeout) {
                throw new IOException(
                        "timeout reached while waiting for the server to be ready");
            }
        }
    }

    public static int waitProcessFinish(Output output, List<String> command)
            throws IOException, InterruptedException {

        return exec(output, command).waitFor();
    }

    public static int waitProcessFinish(
            Output output, String template, Object... params)
                throws IOException, InterruptedException {

        return exec(output, template, params).waitFor();
    }

    public static void checkForASanFindings(Output output) {
        for (Line line : output.lines()) {
            if (line.contains("ERROR: AddressSanitizer:")) {
                throw achtung("hey, AddressSanitizer found something!");
            }
        }
    }
}
