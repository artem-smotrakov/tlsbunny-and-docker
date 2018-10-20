package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.server;

import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.OpensslDocker;
import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class OpensslClient extends OpensslDocker implements Client {

    // TODO: add synchronization
    private Config config = SystemPropertiesConfig.load();

    public OpensslClient() {
        dockerEnvs.put("mode", "client");
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public OpensslClient set(StructFactory factory) {
        throw new UnsupportedOperationException("no factories for you!");
    }

    @Override
    public OpensslClient set(Negotiator negotiator) {
        throw new UnsupportedOperationException("no negotiators for you!");
    }

    @Override
    public OpensslClient set(Analyzer analyzer) {
        throw new UnsupportedOperationException("no analyzers for you!");
    }

    @Override
    public OpensslClient set(Config config) {
        this.config = config;
        return this;
    }

    @Override
    public OpensslClient set(Output output) {
        output.achtung("you can't set output for me!");
        return this;
    }

    @Override
    public OpensslClient set(Check... checks) {
        throw new UnsupportedOperationException("no checks for you!");
    }

    @Override
    public Engine engine() {
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
    public Thread start() {
        if (containerName != null) {
            throw whatTheHell("the client has already been started!");
        }

        createReportDirectory();

        Thread thread = new Thread(this);
        thread.start();

        return thread;
    }

    @Override
    public OpensslClient connect() {
        run();
        return this;
    }

    @Override
    public void run() {
        containerName = generateContainerName();

        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("run");
        command.add("--network");
        command.add("host");
        command.add("-v");
        command.add(String.format("%s:%s",
                host_report_directory, container_report_directory));

        dockerEnvs.put("port", String.valueOf(config.port()));
        for (Map.Entry entry : dockerEnvs.entrySet()) {
            command.add("-e");
            command.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
        }

        // note: -debug and -tlsextdebug options enable more output
        //       (they need to be passed to s_client via "options" variable)

        command.add("--name");
        command.add(containerName);
        command.add("openssl/tls13");

        try {
            int code = Utils.waitProcessFinish(output, command);
            if (code != 0) {
                output.achtung("the client exited with a non-zero exit code (%d)", code);
            }
        } catch (InterruptedException | IOException e) {
            output.achtung("unexpected exception occurred", e);
        }
    }

    @Override
    public OpensslClient stop() {
        if (containerName == null) {
            throw whatTheHell("the client has not been started yet!");
        }

        try {
            List<String> command = List.of(
                    "docker",
                    "exec",
                    containerName,
                    "bash",
                    "-c",
                    "rm /var/src/tlsbunny/stop.file"
            );

            int code = Utils.waitProcessFinish(output, command);
            if (code != 0) {
                output.achtung("could not stop the client (exit code %d)", code);
            }
        } catch (InterruptedException | IOException e) {
            output.achtung("unexpected exception occurred", e);
        }

        return this;
    }

    @Override
    public boolean running() {
        if (containerName == null) {
            return false;
        }

        return containerRunning();
    }

    @Override
    public void close() throws Exception {
        stop();

        Utils.waitStop(this);
        output.info("client stopped");

        if (containerName != null) {
            int code = Utils.waitProcessFinish(output, remove_container_template, containerName);
            if (code != 0) {
                output.achtung("could not remove the container (exit code %d)", code);
            }
        }

        output.flush();
    }

}
