package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.tls13.server.StopCondition;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory.newMutatedStructFactory;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class MutatedServer implements Server {

    public static final int free_port = 0;

    private final ServerSocket serverSocket;
    private final Server server;
    private final List<Engine> engines = Collections.synchronizedList(new ArrayList<>());

    // TODO: synchronization
    private boolean running = false;
    private boolean failed = false;
    private Output output;
    private FuzzerConfig fuzzerConfig;

    public static MutatedServer mutatedServer(
            Server server, FuzzerConfig fuzzerConfig) throws IOException {

        return new MutatedServer(new ServerSocket(free_port), server, fuzzerConfig);
    }

    private MutatedServer(ServerSocket serverSocket,
                          Server server, FuzzerConfig fuzzerConfig) {

        this.server = server;
        this.fuzzerConfig = fuzzerConfig;
        this.serverSocket = serverSocket;
    }

    @Override
    public MutatedServer set(Config config) {
        if (config instanceof FuzzerConfig == false) {
            throw whatTheHell("expected FuzzerConfig!");
        }
        this.fuzzerConfig = (FuzzerConfig) config;
        return this;
    }

    @Override
    public MutatedServer set(EngineFactory engineFactory) {
        throw whatTheHell("you can't set an engine factory for me!");
    }

    @Override
    public MutatedServer set(Check check) {
        throw whatTheHell("you can't set a check for me!");
    }

    @Override
    public MutatedServer stopWhen(StopCondition condition) {
        throw whatTheHell("I know when I should stop!");
    }

    @Override
    public MutatedServer stop() {
        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                output.achtung("exception occurred while stopping the server", e);
            }
        }

        return this;
    }

    @Override
    public boolean running() {
        return running;
    }

    @Override
    public int port() {
        return serverSocket.getLocalPort();
    }

    @Override
    public Engine[] engines() {
        return engines.toArray(new Engine[0]);
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public Server set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public Output output() {
        return output;
    }

    @Override
    public EngineFactory engineFactory() {
        throw whatTheHell("no engine factories for you!");
    }

    @Override
    public void close() throws Exception {
        stop();

        if (output != null) {
            output.flush();
        }
    }

    @Override
    public void run() {
        StructFactory factory = fuzzerConfig.factory();
        if (factory instanceof MutatedStructFactory == false) {
            throw whatTheHell("expected %s", MutatedStructFactory.class.getSimpleName());
        }
        MutatedStructFactory mutatedStructFactory = (MutatedStructFactory) factory;
        mutatedStructFactory.currentTest(fuzzerConfig.startTest());

        EngineFactory engineFactory = server.engineFactory();
        engineFactory.set(mutatedStructFactory);

        output.info("started on port %d", port());

        output.info("run fuzzer config:");
        output.info("\ttarget     = %s", mutatedStructFactory.target());
        output.info("\tfuzzer     = %s",
                mutatedStructFactory.fuzzer() != null
                        ? mutatedStructFactory.fuzzer().toString()
                        : "null");
        output.info("\tstart test = %d", fuzzerConfig.startTest());
        output.info("\tend test   = %d", fuzzerConfig.endTest());

        running = true;
        while (shouldRun(mutatedStructFactory)) {
            try (Connection connection = Connection.create(serverSocket.accept())) {
                output.info("test %d (accepted)", mutatedStructFactory.currentTest());

                Engine engine = engineFactory.create();
                engines.add(engine);

                engine.set(output);
                engine.set(connection);
                engine.connect();

                output.info("done");
            } catch (Exception e) {
                output.achtung("exception: ", e);
                failed = true;
                break;
            } finally {
                output.flush();
            }

            mutatedStructFactory.moveOn();
        }

        for (Engine engine : engines) {
            engine.apply(fuzzerConfig.analyzer());
        }

        running = false;
        output.info("stopped");
    }

    private boolean shouldRun(MutatedStructFactory mutatedStructFactory) {
        return mutatedStructFactory.canFuzz()
                && mutatedStructFactory.currentTest() <= fuzzerConfig.endTest();
    }
}
