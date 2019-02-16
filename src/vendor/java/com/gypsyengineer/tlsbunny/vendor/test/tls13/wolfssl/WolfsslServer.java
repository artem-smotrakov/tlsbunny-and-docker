package com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl;

import com.gypsyengineer.tlsbunny.output.OutputListener;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.AddressSanitizerWatcherOutput;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.BaseDockerServer;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WolfsslServer extends BaseDockerServer implements Server {

    private static final int defaultPort = 40101;

    private static final String image = System.getProperty(
            "tlsbunny.wolfssl.docker.image",
            "artemsmotrakov/tlsbunny_wolfssl_tls13");

    private final OutputListenerImpl listener = new OutputListenerImpl();

    public static WolfsslServer wolfsslServer() {
        return new WolfsslServer();
    }

    private WolfsslServer() {
        output = new AddressSanitizerWatcherOutput();
        output.add(listener);
        output.prefix("wolfssl-server");
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
    public void run() {
        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("run");
        command.add("-p");
        command.add(String.format("%d:%d", defaultPort, defaultPort));

        if (!dockerEnv.isEmpty()) {
            for (Map.Entry entry : dockerEnv.entrySet()) {
                command.add("-e");
                command.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
            }
        }

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
                    if (string.contains("wolfSSL Leaving SSL_new, return 0")) {
                        serverStarted = true;
                    }
                }
            }

            for (String string : strings) {
                if (string.contains("wolfSSL Entering wolfSSL_SetHsDoneCb")) {
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
