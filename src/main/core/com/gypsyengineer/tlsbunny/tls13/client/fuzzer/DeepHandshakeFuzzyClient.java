package com.gypsyengineer.tlsbunny.tls13.client.fuzzer;

import com.gypsyengineer.tlsbunny.fuzzer.Ratio;
import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.client.HttpsClient;
import com.gypsyengineer.tlsbunny.tls13.client.HttpsClientAuth;
import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineException;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.check.SuccessCheck;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzer;
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

    private static final long long_read_timeout = 5000;

    private static final Ratio[] byte_flip_ratios = {
            new Ratio(0.01, 0.02),
            new Ratio(0.02, 0.03),
            new Ratio(0.03, 0.04),
            new Ratio(0.04, 0.05),
            new Ratio(0.05, 0.06),
            new Ratio(0.06, 0.07),
            new Ratio(0.07, 0.08),
            new Ratio(0.08, 0.09),
            new Ratio(0.1, 0.2),
            new Ratio(0.2, 0.3),
            new Ratio(0.3, 0.4),
            new Ratio(0.4, 0.5),
            new Ratio(0.5, 0.6),
            new Ratio(0.6, 0.7),
            new Ratio(0.7, 0.8),
            new Ratio(0.8, 0.9),
            new Ratio(0.9, 1.0),
    };

    private static final Ratio[] bit_flip_ratios = {
            new Ratio(0.01, 0.02),
            new Ratio(0.02, 0.03),
            new Ratio(0.03, 0.04),
            new Ratio(0.04, 0.05),
            new Ratio(0.05, 0.06),
            new Ratio(0.06, 0.07),
            new Ratio(0.07, 0.08),
            new Ratio(0.08, 0.09),
    };

    private Client client;
    private Output output;
    private FuzzerConfig fuzzerConfig;

    private boolean strict = true;

    public static DeepHandshakeFuzzyClient deepHandshakeFuzzyClient(
            Client client, FuzzerConfig fuzzerConfig, Output output) {

        return new DeepHandshakeFuzzyClient(client, fuzzerConfig, output);
    }

    public DeepHandshakeFuzzyClient(
            Client client, FuzzerConfig fuzzerConfig, Output output) {

        this.client = client;
        this.output = output;
        this.fuzzerConfig = fuzzerConfig;
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

    // fuzzer configs

    public static FuzzerConfig[] noClientAuth(Config config) {
        return merge(
                enumerateByteFlipRatios(
                        () -> deepHandshakeFuzzer(),
                        new FuzzerConfig(config)
                                .readTimeout(long_read_timeout)
                                .endTest(2000)
                                .parts(5)),
                enumerateBitFlipRatios(
                        () -> deepHandshakeFuzzer(),
                        new FuzzerConfig(config)
                                .readTimeout(long_read_timeout)
                                .endTest(2000)
                                .parts(5)));
    }

    public static FuzzerConfig[] clientAuth(Config config) {
        return merge(
                enumerateByteFlipRatios(
                        () -> deepHandshakeFuzzer(),
                        new FuzzerConfig(config)
                                .readTimeout(long_read_timeout)
                                .endTest(2000)
                                .parts(5)),
                enumerateBitFlipRatios(
                        () -> deepHandshakeFuzzer(),
                        new FuzzerConfig(config)
                                .readTimeout(long_read_timeout)
                                .endTest(2000)
                                .parts(5)));
    }

    private static FuzzerConfig[] enumerateByteFlipRatios(
            FuzzyStructFactoryBuilder builder, FuzzerConfig... configs) {

        List<FuzzerConfig> generatedConfigs = new ArrayList<>();
        for (FuzzerConfig config : configs) {
            for (Ratio ratio : byte_flip_ratios) {
                FuzzerConfig newConfig = config.copy();
                DeepHandshakeFuzzer deepHandshakeFuzzer = builder.build();
                deepHandshakeFuzzer.fuzzer(newByteFlipFuzzer()
                        .minRatio(ratio.min())
                        .maxRatio(ratio.max()));
                newConfig.factory(deepHandshakeFuzzer);

                generatedConfigs.add(newConfig);
            }
        }

        return generatedConfigs.toArray(new FuzzerConfig[0]);
    }

    private static FuzzerConfig[] enumerateBitFlipRatios(
            FuzzyStructFactoryBuilder builder, FuzzerConfig... configs) {

        List<FuzzerConfig> generatedConfigs = new ArrayList<>();
        for (FuzzerConfig config : configs) {
            for (Ratio ratio : bit_flip_ratios) {
                FuzzerConfig newConfig = config.copy();
                DeepHandshakeFuzzer deepHandshakeFuzzer = builder.build();
                deepHandshakeFuzzer.fuzzer(newBitFlipFuzzer()
                                .minRatio(ratio.min())
                                .maxRatio(ratio.max()));
                newConfig.factory(deepHandshakeFuzzer);

                generatedConfigs.add(newConfig);
            }
        }

        return generatedConfigs.toArray(new FuzzerConfig[0]);
    }

    private static FuzzerConfig[] merge(FuzzerConfig[]... lists) {
        List<FuzzerConfig> result = new ArrayList<>();
        for (FuzzerConfig[] configs : lists) {
            result.addAll(List.of(configs));
        }
        return result.toArray(new FuzzerConfig[0]);
    }

    private interface FuzzyStructFactoryBuilder {
        DeepHandshakeFuzzer build();
    }

}
