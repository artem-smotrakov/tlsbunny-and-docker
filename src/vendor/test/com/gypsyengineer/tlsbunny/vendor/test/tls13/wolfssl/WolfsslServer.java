package com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.tls13.server.StopCondition;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SimpleOutput;
import com.gypsyengineer.tlsbunny.utils.OutputListener;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class WolfsslServer extends WolfsslDocker implements Server {

    public static final int port = 40101;

    private boolean failed = false;
    private int previousAcceptCounter = 0;
    private final OutputListenerImpl listener = new OutputListenerImpl();

    public static WolfsslServer wolfsslServer() {
        return new WolfsslServer();
    }

    private WolfsslServer() {
        output.add(listener);
    }

    @Override
    public boolean ready() {
        synchronized (this) {
            if (previousAcceptCounter != listener.acceptCounter) {
                previousAcceptCounter = listener.acceptCounter;
                return true;
            }
        }

        return false;
    }

    @Override
    public WolfsslServer set(Config config) {
        throw new UnsupportedOperationException("no configs for you!");
    }

    @Override
    public WolfsslServer set(SimpleOutput output) {
        output.achtung("you can't set output for me!");
        return this;
    }

    @Override
    public WolfsslServer set(EngineFactory engineFactory) {
        throw new UnsupportedOperationException("no engine factories for you!");
    }

    @Override
    public WolfsslServer set(Check check) {
        throw new UnsupportedOperationException("no checks for you!");
    }

    @Override
    public WolfsslServer stopWhen(StopCondition condition) {
        throw new UnsupportedOperationException("no stop conditions for you!");
    }

    @Override
    public EngineFactory engineFactory() {
        throw new UnsupportedOperationException("no engine factories for you!");
    }

    @Override
    public Engine[] engines() {
        throw new UnsupportedOperationException("no engines for you!");
    }

    @Override
    public SimpleOutput output() {
        return output;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    synchronized public boolean failed() {
        return failed;
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
        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("run");
        command.add("-p");
        command.add(String.format("%d:%d", port, port));

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
            int code = Utils.waitProcessFinish(output, command);
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
        if (!listener.serverStarted) {
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

        @Override
        synchronized public void receivedInfo(String... strings) {
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
        synchronized public void receivedAchtung(String... strings) {
            // do nothing
        }
    }

}
