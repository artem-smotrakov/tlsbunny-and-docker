package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.output.Output;
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
    protected int previousAcceptCounter = 0;

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
}
