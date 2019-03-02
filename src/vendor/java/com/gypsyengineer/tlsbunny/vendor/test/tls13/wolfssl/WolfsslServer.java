package com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl;

import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.AddressSanitizerWatcherOutput;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.BaseDockerServer;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WolfsslServer extends BaseDockerServer implements Server {

    private static final int defaultPort = 40101;

    private static final String defaultServerCert = "certs/server-cert.pem";
    private static final String defaultServerKey = "certs/server-key.pem";

    private static final String no_arg = "";

    private static final String image = System.getProperty(
            "tlsbunny.wolfssl.docker.image",
            "artemsmotrakov/tlsbunny_wolfssl_tls13");

    private final Map<String, String> options = new HashMap<>();
    private Status status = Status.not_started;

    public static WolfsslServer wolfsslServer() {
        return new WolfsslServer();
    }

    private WolfsslServer() {
        super(new OutputListenerImpl(
                "wolfSSL Leaving SSL_new, return 0",
                        "wolfSSL Entering wolfSSL_SetHsDoneCb"),
                new AddressSanitizerWatcherOutput());
        output.prefix("wolfssl-server");
        options.put("-p", String.valueOf(defaultPort));
        options.put("-v", "4"); // TLS 1.3 only
        options.put("-d", no_arg); // no client auth
        options.put("-i", no_arg);
        options.put("-g", no_arg);
        options.put("-b", no_arg);
        options.put("-x", no_arg);
        options.put("-c", defaultServerCert);
        options.put("-k", defaultServerKey);
    }

    public WolfsslServer clientAuth() {
        options.remove("-d");
        return this;
    }

    @Override
    public int port() {
        return defaultPort;
    }

    @Override
    public void run() {
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
            output.achtung("overwritten environment variable 'options'");
        }
        dockerEnv.put("options", sb.toString());

        if (!dockerEnv.isEmpty()) {
            for (Map.Entry entry : dockerEnv.entrySet()) {
                command.add("-e");
                command.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
            }
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
    public WolfsslServer stop() {
        try {
            List<String> command = List.of(
                    "docker",
                    "exec",
                    containerName,
                    "bash",
                    "-c",
                    "rm /var/src/wolfssl/stop.file; pidof lt-server | xargs kill -9"
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
