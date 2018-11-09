package com.gypsyengineer.tlsbunny.tls13.server.fuzzer;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.tls13.server.StopCondition;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class MutatedServer implements Server {

    private final Server server;

    private Output output;
    private FuzzerConfig fuzzerConfig;

    public MutatedServer(Server server) {
        this.server = server;
    }

    @Override
    public Server set(Config config) {
        if (config instanceof FuzzerConfig == false) {
            throw whatTheHell("expected FuzzerConfig!");
        }
        this.fuzzerConfig = (FuzzerConfig) config;
        return this;
    }

    @Override
    public Server set(EngineFactory engineFactory) {
        server.set(engineFactory);
        return this;
    }

    @Override
    public Server set(Check check) {
        server.set(check);
        return this;
    }

    @Override
    public Server stopWhen(StopCondition condition) {
        server.stopWhen(condition);
        return this;
    }

    @Override
    public Server stop() {
        server.stop();
        return this;
    }

    @Override
    public boolean running() {
        return server.running();
    }

    @Override
    public int port() {
        return server.port();
    }

    @Override
    public Engine[] engines() {
        return server.engines();
    }

    @Override
    public boolean failed() {
        return server.failed();
    }

    @Override
    public Server set(Output output) {
        server.set(output);
        return this;
    }

    @Override
    public Output output() {
        return server.output();
    }

    @Override
    public void close() throws Exception {
        server.close();
    }

    @Override
    public void run() {
        server.run();
    }
}
