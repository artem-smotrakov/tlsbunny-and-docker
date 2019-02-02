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
import com.gypsyengineer.tlsbunny.utils.Sync;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class MutatedServer implements Server {

    public static final int free_port = 0;

    private final ServerSocket ssocket;
    private final Server server;
    private final List<Engine> engines = Collections.synchronizedList(new ArrayList<>());

    // TODO: synchronization
    private boolean running = false;
    private boolean failed = false;
    private Output output;
    private FuzzerConfig fuzzerConfig;
    private Sync sync = Sync.dummy();

    private long test = 0;

    public static MutatedServer mutatedServer(
            Server server, FuzzerConfig fuzzerConfig) throws IOException {

        return new MutatedServer(new ServerSocket(free_port), server, fuzzerConfig);
    }

    private MutatedServer(ServerSocket ssocket,
                          Server server, FuzzerConfig fuzzerConfig) {

        this.server = server;
        this.fuzzerConfig = fuzzerConfig;
        this.ssocket = ssocket;
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
    public MutatedServer set(Sync sync) {
        this.sync = sync;
        return this;
    }

    @Override
    public MutatedServer stopWhen(StopCondition condition) {
        throw whatTheHell("I know when I should stop!");
    }

    @Override
    public MutatedServer stop() {
        if (!ssocket.isClosed()) {
            try {
                ssocket.close();
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
        return ssocket.getLocalPort();
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
    public MutatedServer set(Output output) {
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
    public void close() {
        stop();

        if (output != null) {
            output.flush();
        }
    }

    @Override
    public void run() {
        StructFactory factory = fuzzerConfig.factory();
        if (factory instanceof FuzzyStructFactory == false) {
            throw whatTheHell("expected %s",
                    FuzzyStructFactory.class.getSimpleName());
        }
        FuzzyStructFactory fuzzyStructFactory = (FuzzyStructFactory) factory;

        // TODO: set fuzzer state here

        EngineFactory engineFactory = server.engineFactory();
        engineFactory.set(fuzzyStructFactory);

        output.info("run fuzzer config:");
        output.info("  targets    = %s",
                Arrays.stream(fuzzyStructFactory.targets())
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")));
        output.info("  fuzzer     = %s",
                fuzzyStructFactory.fuzzer() != null
                        ? fuzzyStructFactory.fuzzer().toString()
                        : "null");
        output.info("  total tests = %d", fuzzerConfig.total());

        running = true;
        output.info("started on port %d", port());
        test = 0;
        while (shouldRun(fuzzyStructFactory)) {
            try (Connection connection = Connection.create(ssocket.accept())) {
                String message = String.format("test #%d (accepted), %s/%s, targets: [%s]",
                        test,
                        getClass().getSimpleName(),
                        fuzzyStructFactory.fuzzer().getClass().getSimpleName(),
                        Arrays.stream(fuzzyStructFactory.targets)
                                .map(Enum::toString)
                                .collect(Collectors.joining(", ")));
                output.info(message);

                Engine engine = engineFactory.create()
                        .set(output)
                        .set(connection)
                        .connect();

                engines.add(engine);
            } catch (Exception e) {
                output.achtung("exception: ", e);
                failed = true;
                break;
            } finally {
                output.flush();
            }

            fuzzyStructFactory.moveOn();
            test++;
        }

        running = false;
        output.info("stopped");
    }

    private boolean shouldRun(FuzzyStructFactory mutatedStructFactory) {
        return mutatedStructFactory.canFuzz() && test < fuzzerConfig.total();
    }
}
