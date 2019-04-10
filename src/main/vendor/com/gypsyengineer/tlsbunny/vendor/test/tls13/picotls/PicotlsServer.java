package com.gypsyengineer.tlsbunny.vendor.test.tls13.picotls;

import com.gypsyengineer.tlsbunny.output.InputStreamOutput;
import com.gypsyengineer.tlsbunny.output.Output;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.BaseDockerServer;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class PicotlsServer extends BaseDockerServer implements Server {

    private static final int defaultPort = 20101;

    private static final int stop_wait_delay = 1000;

    private static final String image = System.getProperty(
            "tlsbunny.picotls.docker.image",
            "tlsbunny_picotls_tls13");

    private final Map<String, String> options = new HashMap<>();
    private Status status = Status.not_started;

    public static PicotlsServer picotlsServer() {
        return new PicotlsServer();
    }

    private PicotlsServer() {
        super(new OutputListenerImpl(
                "server started on port", "waiting for connections"));
        output.prefix("picotls-server");
        options.put("-c", "certs/server_cert.pem");
        options.put("-k", "certs/server_key.pem");
        options.put("-i", "message");
        options.put("0.0.0.0", no_arg);
        options.put(String.valueOf(defaultPort), no_arg);
    }

    public PicotlsServer clientAuth() {
        options.put("-a", no_arg);
        return this;
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

        Thread thread = new Thread(this);
        thread.start();

        return thread;
    }

    @Override
    public void run() {
        if (status() != Status.not_started) {
            throw whatTheHell("server can't be started twice!");
        }

        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("run");
        command.add("-p");
        command.add(String.format("%d:%d", defaultPort, defaultPort));

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : options.entrySet()) {
            sb.append(String.format("%s %s ", entry.getKey(), entry.getValue()));
        }
        if (dockerEnv.containsKey("options")) {
            output.achtung("overwrite environment variable 'options', previous value: %s",
                    dockerEnv.get("options"));
        }
        dockerEnv.put("options", sb.toString());

        for (Map.Entry entry : dockerEnv.entrySet()) {
            command.add("-e");
            command.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
        }

        command.add("--name");
        command.add(containerName);
        command.add(image);

        synchronized (this) {
            status = Status.ready;
        }
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
        } finally {
            synchronized (this) {
                status = Status.done;
            }
        }
    }

    @Override
    public Status status() {
        synchronized (this) {
            return status;
        }
    }

    @Override
    public PicotlsServer stop() {
        try {
            List<String> command = List.of(
                    "docker",
                    "exec",
                    containerName,
                    "bash",
                    "-c",
                    "pidof cli | xargs kill -SIGINT"
            );

            synchronized (this) {
                status = Status.ready;
            }
            try {
                Process process = Utils.exec(output, command);
                InputStreamOutput commandOutput = Output.create(process.getInputStream());
                int code = process.waitFor();
                output.add(commandOutput);

                if (code != 0) {
                    output.achtung("could not stop the server (exit code %d)", code);
                    failed = true;
                }

                while (containerRunning()) {
                    Utils.sleep(stop_wait_delay);
                }
            } finally {
                synchronized (this) {
                    status = Status.done;
                }
            }
        } catch (InterruptedException | IOException e) {
            output.achtung("unexpected exception occurred", e);
            failed = true;
        }

        return this;
    }
}
