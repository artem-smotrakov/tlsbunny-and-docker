package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import static com.gypsyengineer.tlsbunny.tls13.client.FuzzyClient.noClientAuthConfigs;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class FuzzyHttpsClient implements Client {

    private Config mainConfig = SystemPropertiesConfig.load();
    private FuzzerConfig[] fuzzerConfigs;
    private Output output;
    private Analyzer analyzer;
    private Check[] checks;

    public static void main(String[] args) throws Exception {
        try (Output output = new Output()) {
            Config config = SystemPropertiesConfig.load();
            new FuzzyHttpsClient()
                    .set(noClientAuthConfigs(config))
                    .set(config)
                    .set(output)
                    .connect();
        }
    }

    @Override
    public Config config() {
        return mainConfig;
    }

    public FuzzyHttpsClient set(FuzzerConfig... fuzzerConfigs) {
        this.fuzzerConfigs = fuzzerConfigs;
        return this;
    }

    @Override
    public Client set(Config mainConfig) {
        this.mainConfig = mainConfig;
        return this;
    }

    @Override
    public Client set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public Client connect() throws Exception {
        if (fuzzerConfigs == null) {
            throw whatTheHell("no fuzzer configs! (null)");
        }

        if (fuzzerConfigs.length == 0) {
            throw whatTheHell("no fuzzer configs! (empty array)");
        }

        new Runner()
                .set(FuzzyClient.fuzzerFactory)
                .set(output)
                .set(fuzzerConfigs)
                .set(checks)
                .set(analyzer)
                .submit();

        return this;
    }

    @Override
    public Client set(Check... checks) {
        this.checks = checks;
        return this;
    }

    @Override
    public Client set(Analyzer analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    @Override
    public Client set(StructFactory factory) {
        throw new UnsupportedOperationException("no factories for you!");
    }

    @Override
    public Engine engine() {
        throw new UnsupportedOperationException("no engines for you!");
    }

    @Override
    public Engine[] engines() {
        throw new UnsupportedOperationException("no engines for you!");
    }

    @Override
    public Client set(Negotiator negotiator) {
        throw new UnsupportedOperationException("no negotiators for you!");
    }

    @Override
    public void close() {
        if (output != null) {
            output.flush();
        }
    }
}
