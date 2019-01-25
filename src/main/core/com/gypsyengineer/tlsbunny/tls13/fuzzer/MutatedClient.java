package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineException;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.check.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.check.SuccessCheck;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class MutatedClient implements Client {

    private static final int max_attempts = 3;
    private static final int delay = 3000; // in millis

    private Client client;
    private Output output;
    private Analyzer analyzer;
    private Check[] checks;
    private FuzzerConfig fuzzerConfig;

    private boolean strict = true;

    public static MutatedClient mutatedClient() {
        return new MutatedClient();
    }

    private MutatedClient() {}

    public MutatedClient(Client client, Output output, FuzzerConfig fuzzerConfig) {
        this.client = client;
        this.output = output;
        this.fuzzerConfig = fuzzerConfig;
    }

    public MutatedClient of(Client client) {
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
    public MutatedClient set(Config config) {
        if (config instanceof FuzzerConfig == false) {
            throw whatTheHell("expected FuzzerConfig!");
        }
        this.fuzzerConfig = (FuzzerConfig) config;
        return this;
    }

    @Override
    public MutatedClient set(StructFactory factory) {
        throw new UnsupportedOperationException("no factories for you!");
    }

    @Override
    public MutatedClient set(Negotiator negotiator) {
        throw new UnsupportedOperationException("no negotiators for you!");
    }

    @Override
    public MutatedClient set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public MutatedClient set(Check... checks) {
        this.checks = checks;
        return this;
    }

    @Override
    public MutatedClient set(Analyzer analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    @Override
    public MutatedClient connect() {
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

    @Override
    public void run() {
        if (fuzzerConfig.noFactory()) {
            throw whatTheHell("no fuzzy set specified!");
        }

        StructFactory factory = fuzzerConfig.factory();
        if (factory instanceof FuzzyStructFactory == false) {
            throw whatTheHell("expected FuzzyStructFactory!");
        }

        FuzzyStructFactory fuzzyStructFactory = (FuzzyStructFactory) factory;
        fuzzyStructFactory.set(output);

        try {
            output.info("run a smoke test before fuzzing");
            client.set(StructFactory.getDefault())
                    .set(fuzzerConfig)
                    .set(output)
                    .set(analyzer)
                    .set(new SuccessCheck())
                    .set(new NoAlertCheck())
                    .connect();
        } catch (Exception e) {
            reportError("smoke test failed", e);
            output.achtung("skip fuzzing");
            return;
        } finally {
            output.flush();
        }
        output.info("smoke test passed, start fuzzing");

        output.info("run fuzzer config:");
        output.info("  targets    = %s",
                Arrays.stream(fuzzyStructFactory.targets())
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")));
        output.info("  fuzzer     = %s",
                fuzzyStructFactory.fuzzer() != null
                        ? fuzzyStructFactory.fuzzer().toString()
                        : "null");
        output.info("  start test = %d", fuzzerConfig.startTest());
        output.info("  end test   = %d", fuzzerConfig.endTest());

        client.set(fuzzyStructFactory)
                .set(fuzzerConfig)
                .set(output)
                .set(analyzer)
                .set(checks);

        try {
            fuzzyStructFactory.currentTest(fuzzerConfig.startTest());
            while (shouldRun(fuzzyStructFactory)) {
                String message = String.format("test #%d, %s/%s, targets: [%s]",
                        fuzzyStructFactory.currentTest(),
                        getClass().getSimpleName(),
                        fuzzyStructFactory.fuzzer().getClass().getSimpleName(),
                        Arrays.stream(fuzzyStructFactory.targets)
                                .map(Enum::toString)
                                .collect(Collectors.joining(", ")));
                output.info(message);

                int attempt = 0;
                while (attempt <= max_attempts) {
                    try {
                        client.connect();
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
                            throw new IOException("looks like the server closed connection");
                        }
                        attempt++;

                        output.achtung("connection failed: %s ", cause.getMessage());
                        output.achtung("let's wait a bit and try again (attempt %d)", attempt);
                        Thread.sleep(delay);
                    } finally {
                        output.flush();
                    }
                }

                output.flush();
                fuzzyStructFactory.moveOn();
            }
        } catch (Exception e) {
            output.achtung("what the hell? unexpected exception", e);
        } finally {
            output.flush();
        }
    }

    private void reportError(String message, Throwable e) {
        output.achtung(message, e);
        if (strict) {
            throw whatTheHell("we failed!", e);
        }
    }

    private boolean shouldRun(FuzzyStructFactory fuzzyStructFactory) {
        return fuzzyStructFactory.canFuzz()
                && fuzzyStructFactory.currentTest() <= fuzzerConfig.endTest();
    }

}
