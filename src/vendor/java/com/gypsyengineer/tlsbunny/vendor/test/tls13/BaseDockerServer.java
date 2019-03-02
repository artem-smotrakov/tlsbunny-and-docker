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

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public abstract class BaseDockerServer extends BaseDocker implements Server {

    protected boolean failed = false;
    private int previousAcceptCounter = 0;
    private final OutputListenerImpl listener;

    public BaseDockerServer(OutputListenerImpl listener, InputStreamOutput output) {
        super(output);
        this.listener = listener;
        output.add(listener);
    }

    public BaseDockerServer(OutputListenerImpl listener) {
        this(listener, new InputStreamOutput());
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

        int code = Utils.waitProcessFinish(output, remove_container_template, containerName);
        if (code != 0) {
            output.achtung("could not remove the container (exit code %d)", code);
            failed = true;
        }

        output.flush();
    }

    @Override
    public Server set(Config config) {
        throw new UnsupportedOperationException("no configs for you!");
    }

    @Override
    public Server set(Output output) {
        output.achtung("you can't set output for me!");
        return this;
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

        Thread thread = new Thread(this);
        thread.start();

        return thread;
    }

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
