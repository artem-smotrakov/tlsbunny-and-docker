package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.output.InputStreamOutput;
import com.gypsyengineer.tlsbunny.output.Output;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.BaseDockerServer;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class OpensslServer extends BaseDockerServer implements Server {

    private static final int defaultPort = 10101;

    private static final int stop_wait_delay = 1000;

    private static final String image = System.getProperty(
            "tlsbunny.openssl.docker.image",
            "tlsbunny_openssl_tls13");

    private static final String host_report_directory = String.format(
            "%s/openssl_report", System.getProperty("user.dir"));

    private final Map<String, String> options = new HashMap<>();
    private Status status = Status.not_started;

    public static OpensslServer opensslServer() {
        return new OpensslServer();
    }

    private OpensslServer() {
        super(new OutputListenerImpl("ACCEPT", "tlsbunny: accept"));
        output.prefix("openssl-server");
        options.put("-key", "certs/server_key.pem");
        options.put("-cert", "certs/server_cert.der");
        options.put("-certform", "der");
        options.put("-accept", String.valueOf(defaultPort));
        options.put("-www", no_arg);
        options.put("-tls1_3", no_arg);
    }

    public OpensslServer noTLSv13() {
        options.remove("-tls1_3");
        return this;
    }

    public OpensslServer minTLSv1() {
        return minProtocol("TLSv1");
    }

    public OpensslServer maxTLSv13() {
        return maxProtocol("TLSv1.3");
    }

    public OpensslServer minProtocol(String value) {
        options.put("-min_protocol", value);
        return this;
    }

    public OpensslServer enableDebugOutput() {
        options.put("-debug", no_arg);
        return this;
    }

    public OpensslServer enableExtDebugOutput() {
        options.put("-tlsextdebug", no_arg);
        return this;
    }

    public OpensslServer maxProtocol(String value) {
        options.put("-max_protocol", value);
        return this;
    }

    public OpensslServer clientAuth() {
        options.put("-Verify", "0");
        options.put("-CAfile", "certs/root_cert.pem");
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

        createReportDirectory(host_report_directory);

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
        command.add("-v");
        command.add(String.format("%s:%s",
                host_report_directory, container_report_directory));

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
            status = Server.Status.ready;
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
                status = Server.Status.done;
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

            synchronized (this) {
                status = Server.Status.ready;
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
                    status = Server.Status.done;
                }
            }
        } catch (InterruptedException | IOException e) {
            output.achtung("unexpected exception occurred", e);
            failed = true;
        }

        return this;
    }
}
