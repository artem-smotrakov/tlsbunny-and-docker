package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.fuzzer.Ratio;
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
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.Utils;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.gypsyengineer.tlsbunny.fuzzer.BitFlipFuzzer.newBitFlipFuzzer;
import static com.gypsyengineer.tlsbunny.fuzzer.ByteFlipFuzzer.newByteFlipFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzer.deepHandshakeFuzzer;
import static com.gypsyengineer.tlsbunny.utils.Achtung.achtung;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class DeepHandshakeFuzzyClient implements Client {

    private static final int max_attempts = 3;
    private static final int delay = 3000; // in millis

    private Client client;
    private Output output;
    private FuzzerConfig fuzzerConfig;

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
        throw new UnsupportedOperationException("no check for you!");
    }

    @Override
    public DeepHandshakeFuzzyClient set(Analyzer analyzer) {
        fuzzerConfig.analyzer(analyzer);
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

            for (Engine engine : engines) {
                engine.run(new SuccessCheck());
            }
        } catch (Exception e) {
            reportError("smoke test failed", e);
            output.achtung("skip fuzzing");
            return;
        } finally {
            output.flush();
        }
        output.info("smoke test passed, start fuzzing");

        if (deepHandshakeFuzzer.targeted().length == 0) {
            throw achtung("no targets found!");
        }

        String targets = Arrays.stream(deepHandshakeFuzzer.targeted())
                .map(type -> type.toString())
                .collect(Collectors.joining( ", " ));

        output.info("run fuzzer config:");
        output.info("\ttargets    = %s", targets);
        output.info("\tfuzzer     = %s",
                deepHandshakeFuzzer.fuzzer() != null
                        ? deepHandshakeFuzzer.fuzzer().toString()
                        : "null");
        output.info("\tstart test = %d", fuzzerConfig.startTest());
        output.info("\tend test   = %d", fuzzerConfig.endTest());

        try {
            deepHandshakeFuzzer.fuzzing();
            deepHandshakeFuzzer.currentTest(fuzzerConfig.startTest());
            while (shouldRun(deepHandshakeFuzzer)) {
                output.info("test %d", deepHandshakeFuzzer.currentTest());

                int attempt = 0;
                while (true) {
                    try {
                        client.set(deepHandshakeFuzzer)
                                .set(fuzzerConfig)
                                .set(output)
                                .set(fuzzerConfig.checks())
                                .connect();

                        break;
                    } catch (EngineException e) {
                        Throwable cause = e.getCause();
                        if (cause instanceof ConnectException == false) {
                            throw e;
                        }

                        if (attempt == max_attempts) {
                            throw new IOException("looks like the server closed connection");
                        }
                        attempt++;

                        output.info("connection failed: %s ", cause.getMessage());
                        output.info("let's wait a bit and try again (attempt %d)", attempt);
                        Utils.sleep(delay);
                    }
                }

                output.flush();
                deepHandshakeFuzzer.moveOn();
            }

            for (Engine engine : client.engines()) {
                engine.apply(fuzzerConfig.analyzer());
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

    private boolean shouldRun(DeepHandshakeFuzzer fuzzer) {
        return fuzzer.canFuzz() && fuzzer.currentTest() <= fuzzerConfig.endTest();
    }

}
