package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.output.OutputListener;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.BaseDockerServer;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class OpensslServer extends BaseDockerServer implements Server {

    public static final int defaultPort = 10101;

    protected static final String image = System.getProperty(
            "tlsbunny.openssl.docker.image",
            "artemsmotrakov/tlsbunny_openssl_tls13");

    private static final String host_report_directory = String.format(
            "%s/openssl_report", System.getProperty("user.dir"));

    private int previousAcceptCounter = 0;
    private final OutputListenerImpl listener = new OutputListenerImpl();

    public static OpensslServer opensslServer() {
        return new OpensslServer();
    }

    private OpensslServer() {
        output.add(listener);
        output.prefix("openssl-server");
    }

    @Override
    public boolean ready() {
        output.update();

        synchronized (this) {
            int counter = listener.acceptCounter();
            if (previousAcceptCounter != counter) {
                previousAcceptCounter = counter;
                return true;
            }
        }

        return false;
    }

    @Override
    public int port() {
        return defaultPort;
    }

    @Override
    public Thread start() {
        if (running()) {
            throw whatTheHell("the server has already been started!");
        }

        createReportDirectory(host_report_directory);

        Thread thread = new Thread(this);
        thread.start();

        return thread;
    }

    @Override
    public void run() {
        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("run");
        command.add("-p");
        command.add(String.format("%d:%d", defaultPort, defaultPort));
        command.add("-v");
        command.add(String.format("%s:%s",
                host_report_directory, container_report_directory));

        if (!dockerEnv.isEmpty()) {
            for (Map.Entry entry : dockerEnv.entrySet()) {
                command.add("-e");
                command.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
            }
        }

        // note: -debug and -tlsextdebug options enable more output
        //       (they need to be passed to s_server via "options" variable)

        command.add("--name");
        command.add(containerName);
        command.add(image);

        try {
            Process process = Utils.exec(output, command);
            output.set(process.getInputStream());
            int code = process.waitFor();
            if (code != 0) {
                output.achtung("the server exited with a non-zero exit code (%d)", code);

                synchronized (this) {
                    failed = true;
                }
            }
        } catch (InterruptedException | IOException e) {
            output.achtung("unexpected exception occurred", e);

            synchronized (this) {
                failed = true;
            }
        }
    }

    @Override
    public OpensslServer stop() {
        try {
            List<String> command = List.of(
                    "docker",
                    "exec",
                    containerName,
                    "bash",
                    "-c",
                    "pidof openssl | xargs kill -SIGINT"
            );

            int code = Utils.waitProcessFinish(output, command);
            if (code != 0) {
                output.achtung("could not stop the server (exit code %d)", code);
                failed = true;
            }
        } catch (InterruptedException | IOException e) {
            output.achtung("unexpected exception occurred", e);
            failed = true;
        }

        return this;
    }

    @Override
    public boolean running() {
        output.update();

        if (!listener.serverStarted()) {
            return false;
        }

        return containerRunning();
    }

    @Override
    public void close() throws Exception {
        stop();

        Utils.waitStop(this);
        output.info("server stopped");

        int code = Utils.waitProcessFinish(output, remove_container_template, containerName);
        if (code != 0) {
            output.achtung("could not remove the container (exit code %d)", code);
            failed = true;
        }

        output.flush();
    }

    private static class OutputListenerImpl implements OutputListener {

        private int acceptCounter = 0;
        private boolean serverStarted = false;

        synchronized int acceptCounter() {
            return acceptCounter;
        }

        synchronized boolean serverStarted() {
            return serverStarted;
        }

        @Override
        public synchronized void receivedInfo(String... strings) {
            if (!serverStarted) {
                for (String string : strings) {
                    if (string.contains("ACCEPT")) {
                        serverStarted = true;
                    }
                }
            }

            for (String string : strings) {
                if (string.contains("tlsbunny: accept")) {
                    acceptCounter++;
                }
            }
        }

        @Override
        public void receivedImportant(String... strings) {
            // do nothing
        }

        @Override
        public synchronized void receivedAchtung(String... strings) {
            // do nothing
        }
    }

}
