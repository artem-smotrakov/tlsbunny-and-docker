package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineException;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.check.SuccessCheck;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.output.Output;
import com.gypsyengineer.tlsbunny.utils.Sync;
import com.gypsyengineer.tlsbunny.utils.Utils;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.gypsyengineer.tlsbunny.utils.Achtung.achtung;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class DeepHandshakeFuzzyClient implements Client {

    private static final int max_attempts = 3;
    private static final int delay = 3000; // in millis

    private Client client;
    private Output output;
    private Check[] checks;
    private Analyzer analyzer;
    private FuzzerConfig fuzzerConfig;
    private Sync sync = Sync.dummy();

    private long test = 0;

    private boolean strict = true;

    public static DeepHandshakeFuzzyClient deepHandshakeFuzzyClient() {
        return new DeepHandshakeFuzzyClient();
    }

    public static DeepHandshakeFuzzyClient deepHandshakeFuzzyClient(
            Client client, FuzzerConfig fuzzerConfig, Output output) {

        return new DeepHandshakeFuzzyClient(client, fuzzerConfig, output);
    }

    private DeepHandshakeFuzzyClient() {}

    public DeepHandshakeFuzzyClient(
            Client client, FuzzerConfig fuzzerConfig, Output output) {

        this.client = client;
        this.output = output;
        this.fuzzerConfig = fuzzerConfig;
    }

    public DeepHandshakeFuzzyClient of(Client client) {
        this.client = client;
        return this;
    }

    @Override
    public Output output() {
        return output;
    }

    @Override
    public Config config() {
        return fuzzerConfig;
    }

    @Override
    public DeepHandshakeFuzzyClient set(Config config) {
        if (config instanceof FuzzerConfig == false) {
            throw whatTheHell("expected FuzzerConfig!");
        }
        this.fuzzerConfig = (FuzzerConfig) config;
        return this;
    }

    @Override
    public DeepHandshakeFuzzyClient set(StructFactory factory) {
        throw new UnsupportedOperationException("no factories for you!");
    }

    @Override
    public DeepHandshakeFuzzyClient set(Negotiator negotiator) {
        throw new UnsupportedOperationException("no negotiators for you!");
    }

    @Override
    public DeepHandshakeFuzzyClient set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public DeepHandshakeFuzzyClient set(Check... checks) {
        this.checks = checks;
        return this;
    }

    @Override
    public DeepHandshakeFuzzyClient set(Analyzer analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    @Override
    synchronized public DeepHandshakeFuzzyClient set(Sync sync) {
        Objects.requireNonNull(sync, "sync can't be null!");
        this.sync = sync;
        return this;
    }

    @Override
    public DeepHandshakeFuzzyClient connect() {
        run();
        return this;
    }

    @Override
    public Engine[] engines() {
        throw new UnsupportedOperationException("no engines for you!");
    }

    @Override
    public void close() {
        if (output != null) {
            output.flush();
        }
    }

    synchronized public Sync sync() {
        return sync;
    }

    @Override
    public void run() {
        if (fuzzerConfig.noFactory()) {
            throw whatTheHell("no factory provided!");
        }

        StructFactory factory = fuzzerConfig.factory();
        if (factory instanceof DeepHandshakeFuzzer == false) {
            throw whatTheHell("expected DeepHandshakeFuzzer!");
        }

        DeepHandshakeFuzzer deepHandshakeFuzzer = (DeepHandshakeFuzzer) factory;
        deepHandshakeFuzzer.set(output);

        deepHandshakeFuzzer.recording();
        sync().start();
        try {
            output.info("run a smoke test before fuzzing");
            Engine[] engines = client.set(StructFactory.getDefault())
                    .set(fuzzerConfig)
                    .set(output)
                    .set(deepHandshakeFuzzer)
                    .connect()
                    .engines();

            if (engines == null || engines.length == 0) {
                throw whatTheHell("no engines!");
            }

            if (analyzer != null) {
                analyzer.add(engines);
            }

            for (Engine engine : engines) {
                engine.run(new SuccessCheck());
            }

            output.info("smoke test passed, start fuzzing");
        } catch (Exception e) {
            reportError("smoke test failed", e);
            output.achtung("skip fuzzing");
            return;
        } finally {
            output.flush();
            sync().end();
        }

        if (fuzzerConfig.hasState()) {
            deepHandshakeFuzzer.state(fuzzerConfig.state());
        }

        if (deepHandshakeFuzzer.targeted().length == 0) {
            throw achtung("no targets found!");
        }

        String targets = Arrays.stream(deepHandshakeFuzzer.targeted())
                .map(Object::toString)
                .collect(Collectors.joining( ", " ));

        output.info("run fuzzer config:");
        output.increaseIndent();
        output.info("targets     = %s", targets);
        output.info("fuzzer      = %s",
                deepHandshakeFuzzer.fuzzer() != null
                        ? deepHandshakeFuzzer.fuzzer().toString()
                        : "null");
        output.info("total tests = %d", fuzzerConfig.total());
        output.info("state       = %s",
                fuzzerConfig.hasState() ? fuzzerConfig.state() : "not specified");
        output.decreaseIndent();

        try {
            deepHandshakeFuzzer.fuzzing();

            test = 0;
            while (shouldRun(deepHandshakeFuzzer)) {
                sync().start();
                try {
                    run(deepHandshakeFuzzer);
                } finally {
                    output.flush();
                    sync().end();
                    deepHandshakeFuzzer.moveOn();
                    test++;
                }
            }
        } catch (Exception e) {
            output.achtung("what the hell? unexpected exception", e);
        } finally {
            output.flush();
        }
    }

    private void run(DeepHandshakeFuzzer deepHandshakeFuzzer) throws Exception {
        String message = String.format("test #%d, %s/%s, targeted: [%s]",
                test,
                deepHandshakeFuzzer.getClass().getSimpleName(),
                deepHandshakeFuzzer.fuzzer().getClass().getSimpleName(),
                Arrays.stream(deepHandshakeFuzzer.targeted())
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")));
        output.info(message);
        output.info("state: %s", deepHandshakeFuzzer.state());

        int attempt = 0;
        while (true) {
            try {
                client.set(deepHandshakeFuzzer)
                        .set(fuzzerConfig)
                        .set(output)
                        .set(checks)
                        .set(analyzer)
                        .connect();

                break;
            } catch (EngineException e) {
                Throwable cause = e.getCause();
                if (cause instanceof ConnectException == false) {
                    // an EngineException may occur due to multiple reasons
                    // if the exception was not caused by ConnectException
                    // we tolerate EngineException here to let the fuzzer to continue
                    output.achtung("an exception occurred, but we continue fuzzing", e);
                    break;
                }

                // if the exception was caused by ConnectException
                // then we try again several times

                if (attempt == max_attempts) {
                    throw new IOException("looks like the server is not responding");
                }
                attempt++;

                output.info("connection failed: %s ", cause.getMessage());
                output.info("let's wait a bit and try again (attempt %d)", attempt);
                Utils.sleep(delay);
            } finally {
                output.flush();
            }
        }
    }

    private void reportError(String message, Throwable e) {
        output.achtung(message, e);
        if (strict) {
            throw whatTheHell("we failed!", e);
        }
    }

    private boolean shouldRun(DeepHandshakeFuzzer fuzzer) {
        return fuzzer.canFuzz() && test < fuzzerConfig.total();
    }

}
