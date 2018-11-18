package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.server;

import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.OpensslDocker;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.tls13.server.StopCondition;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.OutputListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class OpensslServer extends OpensslDocker implements Server {

    public static final int port = 10101;

    private boolean failed = false;
    private int previousAcceptCounter = 0;
    private final OutputListenerImpl listener = new OutputListenerImpl();

    public static OpensslServer opensslServer() {
        return new OpensslServer();
    }

    private OpensslServer() {
        output.add(listener);
    }

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
    public OpensslServer set(Config config) {
        throw new UnsupportedOperationException("no configs for you!");
    }

    @Override
    public OpensslServer set(Output output) {
        output.achtung("you can't set output for me!");
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
    public EngineFactory engineFactory() {
        throw new UnsupportedOperationException("no engine factories for you!");
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
    synchronized public boolean failed() {
        return failed;
    }

    @Override
    public Thread start() {
        if (running()) {
            throw whatTheHell("the server has already been started!");
        }

        createReportDirectory();

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

        int acceptCounter = 0;
        boolean serverStarted = false;

        @Override
        synchronized public void receivedInfo(String... strings) {
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
        synchronized public void receivedAchtung(String... strings) {
            // do nothing
        }
    }

}
