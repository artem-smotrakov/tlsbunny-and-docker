package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl;

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

    public static OpensslServer opensslServer() {
        return new OpensslServer();
    }

    private OpensslServer() {
        super(new OutputListenerImpl("ACCEPT", "tlsbunny: accept"));
        output.prefix("openssl-server");
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
}
