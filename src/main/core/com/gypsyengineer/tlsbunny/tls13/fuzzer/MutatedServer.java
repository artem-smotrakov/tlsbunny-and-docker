package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.tls13.server.StopCondition;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory.newMutatedStructFactory;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class MutatedServer implements Server {

    private final Server server;

    private Output output;
    private FuzzerConfig fuzzerConfig;

    public static MutatedServer mutatedServer(
            Server server, FuzzerConfig fuzzerConfig) {

        server.engineFactory().set(newMutatedStructFactory());
        return new MutatedServer(server, fuzzerConfig);
    }

    private MutatedServer(Server server, FuzzerConfig fuzzerConfig) {
        this.server = server;
        this.fuzzerConfig = fuzzerConfig;
    }

    @Override
    public MutatedServer set(Config config) {
        if (config instanceof FuzzerConfig == false) {
            throw whatTheHell("expected FuzzerConfig!");
        }
        this.fuzzerConfig = (FuzzerConfig) config;
        server.set(config);
        return this;
    }

    @Override
    public MutatedServer set(EngineFactory engineFactory) {
        throw whatTheHell("you can't set an engine factory for me!");
    }

    @Override
    public MutatedServer set(Check check) {
        server.set(check);
        return this;
    }

    @Override
    public MutatedServer stopWhen(StopCondition condition) {
        server.stopWhen(condition);
        return this;
    }

    @Override
    public MutatedServer stop() {
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
        this.output = output;
        server.set(output);
        return this;
    }

    @Override
    public Output output() {
        return output;
    }

    @Override
    public EngineFactory engineFactory() {
        return server.engineFactory();
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
