package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.output.InputStreamOutput;
import com.gypsyengineer.tlsbunny.output.Output;
import com.gypsyengineer.tlsbunny.output.OutputListener;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.tls13.server.StopCondition;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Sync;

import java.io.IOException;
import java.util.*;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public abstract class BaseDockerServer extends BaseDocker implements Server {

    private static final int stop_wait_delay = 1000;

    private final OutputListenerImpl listener;
    private final int dockerPort;
    private final String dockerImage;

    private int previousAcceptCounter = 0;
    private Status status = Status.not_started;
    protected boolean failed = false;
    protected final Map<String, String> options = new LinkedHashMap<>();

    private BaseDockerServer(OutputListenerImpl listener, InputStreamOutput output,
                            int dockerPort, String dockerImage) {
        super(output);
        this.listener = listener;
        this.dockerPort = dockerPort;
        this.dockerImage = dockerImage;
        output.add(listener);
    }

    public BaseDockerServer(OutputListenerImpl listener,
                            int dockerPort, String dockerImage) {
        this(listener, new InputStreamOutput(), dockerPort, dockerImage);
    }

    @Override
    public Status status() {
        synchronized (this) {
            return status;
        }
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

        output.flush();
    }

    @Override
    public Server set(Config config) {
        throw new UnsupportedOperationException("no configs for you!");
    }

    @Override
    public Server set(Output output) {
        throw new UnsupportedOperationException("you can't set output for me!");
    }

    @Override
    public Server set(EngineFactory engineFactory) {
        throw new UnsupportedOperationException("no engine factories for you!");
    }

    @Override
    public Server set(Check check) {
        throw new UnsupportedOperationException("no checks for you!");
    }

    @Override
    public Server stopWhen(StopCondition condition) {
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
    public Server set(Sync sync) {
        // do nothing
        return this;
    }

    @Override
    public synchronized boolean failed() {
        return failed;
    }

    @Override
    public Output output() {
        return output;
    }

    @Override
    public Thread start() {
        if (running()) {
            throw whatTheHell("the server has already been started!");
        }

        if (mountReportDirectory) {
            createReportDirectory(host_report_directory);
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
        command.add(String.format("%d:%d", dockerPort, dockerPort));

        if (mountReportDirectory) {
            command.add("-v");
            command.add(String.format("%s:%s",
                    host_report_directory, container_report_directory));
        }

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
        command.add(dockerImage);

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
    public BaseDockerServer stop() {
        try {
            List<String> command = List.of(
                    "docker",
                    "exec",
                    containerName,
                    "bash",
                    "-c",
                    killCommand()
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

    protected abstract String killCommand();

    public static class OutputListenerImpl implements OutputListener {

        private int acceptCounter = 0;
        private boolean serverStarted = false;

        private final String serverStartedString;
        private final String acceptedString;

        public OutputListenerImpl(String serverStartedString, String acceptedString) {
            this.serverStartedString = serverStartedString;
            this.acceptedString = acceptedString;
        }

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
                    if (string.contains(serverStartedString)) {
                        serverStarted = true;
                    }
                }
            }

            for (String string : strings) {
                if (string.contains(acceptedString)) {
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
