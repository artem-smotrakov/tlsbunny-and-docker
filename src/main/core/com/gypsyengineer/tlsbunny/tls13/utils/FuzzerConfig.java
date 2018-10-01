package com.gypsyengineer.tlsbunny.tls13.utils;

import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.FuzzyStructFactory;
import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.utils.Config;

import java.util.Arrays;
import java.util.Objects;

public class FuzzerConfig implements Config {

    private Config mainConfig;
    private Analyzer analyzer;
    private Check[] checks;
    private FuzzyStructFactory factory;
    private Client client;

    public FuzzerConfig(Config mainConfig) {
        this.mainConfig = mainConfig.copy();
    }

    @Override
    public FuzzerConfig copy() {
        FuzzerConfig clone = new FuzzerConfig(mainConfig);
        clone.analyzer = analyzer;
        clone.checks = checks.clone();
        clone.factory = factory;
        clone.client = client;

        return clone;
    }

    public FuzzerConfig set(Config mainConfig) {
        this.mainConfig = mainConfig;
        return this;
    }

    @Override
    public String host() {
        return mainConfig.host();
    }

    @Override
    public int port() {
        return mainConfig.port();
    }

    @Override
    public double minRatio() {
        return mainConfig.minRatio();
    }

    @Override
    public double maxRatio() {
        return mainConfig.maxRatio();
    }

    @Override
    public int threads() {
        return mainConfig.threads();
    }

    @Override
    public int parts() {
        return mainConfig.parts();
    }

    @Override
    public long startTest() {
        return mainConfig.startTest();
    }

    @Override
    public long endTest() {
        return mainConfig.endTest();
    }

    @Override
    public String clientCertificate() {
        return mainConfig.clientCertificate();
    }

    @Override
    public String clientKey() {
        return mainConfig.clientKey();
    }

    @Override
    public String serverCertificate() {
        return mainConfig.serverCertificate();
    }

    @Override
    public String serverKey() {
        return mainConfig.serverKey();
    }

    @Override
    public String targetFilter() {
        return mainConfig.targetFilter();
    }

    @Override
    public long readTimeout() {
        return mainConfig.readTimeout();
    }

    @Override
    public Config host(String host) {
        mainConfig.host(host);
        return this;
    }

    @Override
    public Config port(int port) {
        mainConfig.port(port);
        return this;
    }

    @Override
    public FuzzerConfig minRatio(double minRatio) {
        mainConfig.minRatio(minRatio);
        return this;
    }

    @Override
    public FuzzerConfig maxRatio(double maxRatio) {
        mainConfig.maxRatio(maxRatio);
        return this;
    }

    @Override
    public FuzzerConfig startTest(long test) {
        mainConfig.startTest(test);
        return this;
    }

    @Override
    public FuzzerConfig endTest(long test) {
        mainConfig.endTest(test);
        return this;
    }

    @Override
    public FuzzerConfig parts(int parts) {
        mainConfig.parts(parts);
        return this;
    }

    @Override
    public FuzzerConfig readTimeout(long timeout) {
        mainConfig.readTimeout(timeout);
        return this;
    }

    @Override
    public Config clientCertificate(String path) {
        mainConfig.clientCertificate(path);
        return this;
    }

    @Override
    public Config clientKey(String path) {
        mainConfig.clientKey(path);
        return this;
    }

    @Override
    public Config serverCertificate(String path) {
        mainConfig.serverCertificate(path);
        return this;
    }

    @Override
    public Config serverKey(String path) {
        mainConfig.serverKey(path);
        return this;
    }

    public FuzzerConfig analyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    public boolean hasAnalyzer() {
        return analyzer != null;
    }

    public Analyzer analyzer() {
        return analyzer;
    }

    public FuzzerConfig set(Check... checks) {
        this.checks = checks;
        return this;
    }

    public Check[] checks() {
        return checks;
    }

    public boolean noFactory() {
        return factory == null;
    }

    public FuzzerConfig factory(FuzzyStructFactory factory) {
        this.factory = factory;
        return this;
    }

    public FuzzyStructFactory factory() {
        return factory;
    }

    public boolean noClient() {
        return client == null;
    }

    public FuzzerConfig client(Client client) {
        this.client = client;
        return this;
    }

    public Client client() {
        return client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FuzzerConfig that = (FuzzerConfig) o;
        return Objects.equals(mainConfig, that.mainConfig) &&
                Objects.equals(analyzer, that.analyzer) &&
                Arrays.equals(checks, that.checks) &&
                Objects.equals(factory, that.factory) &&
                Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mainConfig, analyzer, factory, client);
        result = 31 * result + Arrays.hashCode(checks);
        return result;
    }
}
