package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.tls13.server.StopCondition;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.waitServerStop;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class OpensslServer implements Server, AutoCloseable {

    public static final int port = 10101;

    private static final String remove_container_template =
            "docker container rm %s";

    private static final String host_report_directory = String.format(
            "%s/openssl_report", System.getProperty("user.dir"));

    private static final String container_report_directory = "/var/reports";

    // TODO: add synchronization
    private String containerName;
    private boolean failed = false;
    private final Output output = new Output("openssl_server");
    private Map<String, String> dockerEnvs = new HashMap<>();
    private int acceptCounter = 0;

    public OpensslServer dockerEnv(String name, String value) {
        dockerEnvs.put(name, value);
        return this;
    }

    public boolean ready() {
        List<String> strings = output.strings();
        int acceptCounter = 0;
        for (String string : strings) {
            if (string.contains("tlsbunny: accept")) {
                acceptCounter++;
            }
        }

        if (acceptCounter != this.acceptCounter) {
            this.acceptCounter = acceptCounter;
            return true;
        }

        return false;
    }

    @Override
    public OpensslServer set(Config config) {
        throw new UnsupportedOperationException("no configs for you!");
    }

    @Override
    public OpensslServer set(Output output) {
        return this;
    }

    @Override
    public OpensslServer set(EngineFactory engineFactory) {
        throw new UnsupportedOperationException("no engine factories for you!");
    }

    @Override
    public OpensslServer set(Check check) {
        throw new UnsupportedOperationException("no checks for you!");
    }

    @Override
    public OpensslServer stopWhen(StopCondition condition) {
        throw new UnsupportedOperationException("no stop conditions for you!");
    }

    @Override
    public Engine recentEngine() {
        throw new UnsupportedOperationException("no engines for you!");
    }

    @Override
    public Engine[] engines() {
        throw new UnsupportedOperationException("no engines for you!");
    }

    @Override
    public Output output() {
        return output;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public Thread start() {
        if (containerName != null) {
            throw whatTheHell("the server has already been started!");
        }

        createReportDirectory();

        Thread thread = new Thread(this);
        thread.start();

        return thread;
    }

    @Override
    public void run() {
        containerName = generateContainerName();

        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("run");
        command.add("-p");
        command.add(String.format("%d:%d", port, port));
        command.add("-v");
        command.add(String.format("%s:%s",
                host_report_directory, container_report_directory));

        if (!dockerEnvs.isEmpty()) {
            for (Map.Entry entry : dockerEnvs.entrySet()) {
                command.add("-e");
                command.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
            }
        }

        // note: -debug and -tlsextdebug options enable more output
        //       (they need to be passed to s_server via "options" variable)

        command.add("--name");
        command.add(containerName);
        command.add("openssl/server/tls13");

        try {
            int code = Utils.waitProcessFinish(output, command);
            if (code != 0) {
                output.achtung("the server exited with a non-zero exit code (%d)", code);
                failed = true;
            }
        } catch (InterruptedException | IOException e) {
            output.achtung("unexpected exception occurred", e);
            failed = true;
        }
    }

    @Override
    public OpensslServer stop() {
        if (containerName == null) {
            throw whatTheHell("the server has not been started yet!");
        }

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
        if (containerName == null) {
            return false;
        }

        if (!output.contains("ACCEPT")) {
            return false;
        }

        return containerRunning();
    }

    @Override
    public void close() throws Exception {
        stop();

        waitServerStop(this);
        output.info("server stopped");

        if (containerName != null) {
            int code = Utils.waitProcessFinish(output, remove_container_template, containerName);
            if (code != 0) {
                output.achtung("could not remove the container (exit code %d)", code);
                failed = true;
            }
        }

        output.flush();
    }

    private boolean containerRunning() {
        try {
            List<String> command = List.of(
                    "/bin/bash",
                    "-c",
                    String.format("docker container ps | grep %s", containerName)
            );
            return Utils.exec(output, command).waitFor() == 0;
        } catch (InterruptedException | IOException e) {
            failed = true;
            throw new RuntimeException("unexpected exception occurred", e);
        }
    }

    private void createReportDirectory() {
        try {
            Files.createDirectories(Paths.get(host_report_directory));
        } catch (IOException e) {
            throw whatTheHell("could not create a directory for reports!", e);
        }
    }

    private static String generateContainerName() {
        return String.format("%s_%d",
                OpensslServer.class.getSimpleName().toLowerCase(),
                System.currentTimeMillis());
    }

}
