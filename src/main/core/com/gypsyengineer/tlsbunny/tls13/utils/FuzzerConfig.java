package com.gypsyengineer.tlsbunny.tls13.utils;

import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;

import java.util.Arrays;
import java.util.Objects;

public class FuzzerConfig implements Config {

    private Config mainConfig;
    private Analyzer analyzer;
    private Check[] checks;
    private StructFactory factory;
    private Client client;

    public FuzzerConfig(Config mainConfig) {
        this.mainConfig = mainConfig.copy();
    }

    @Override
    synchronized public FuzzerConfig copy() {
        FuzzerConfig clone = new FuzzerConfig(mainConfig);
        clone.analyzer = analyzer;
        clone.checks = checks != null ? checks.clone() : null;
        clone.factory = factory;
        clone.client = client;

        return clone;
    }

    synchronized public FuzzerConfig set(Config mainConfig) {
        this.mainConfig = mainConfig;
        return this;
    }

    @Override
    synchronized public String host() {
        return mainConfig.host();
    }

    @Override
    synchronized public int port() {
        return mainConfig.port();
    }

    @Override
    synchronized public double minRatio() {
        return mainConfig.minRatio();
    }

    @Override
    synchronized public double maxRatio() {
        return mainConfig.maxRatio();
    }

    @Override
    synchronized public int threads() {
        return mainConfig.threads();
    }

    @Override
    synchronized public int parts() {
        return mainConfig.parts();
    }

    @Override
    synchronized public long startTest() {
        return mainConfig.startTest();
    }

    @Override
    synchronized public long endTest() {
        return mainConfig.endTest();
    }

    @Override
    synchronized public String clientCertificate() {
        return mainConfig.clientCertificate();
    }

    @Override
    synchronized public String clientKey() {
        return mainConfig.clientKey();
    }

    @Override
    synchronized public String serverCertificate() {
        return mainConfig.serverCertificate();
    }

    @Override
    synchronized public String serverKey() {
        return mainConfig.serverKey();
    }

    @Override
    synchronized public String targetFilter() {
        return mainConfig.targetFilter();
    }

    @Override
    synchronized public long readTimeout() {
        return mainConfig.readTimeout();
    }

    @Override
    synchronized public Config host(String host) {
        mainConfig.host(host);
        return this;
    }

    @Override
    synchronized public Config port(int port) {
        mainConfig.port(port);
        return this;
    }

    @Override
    synchronized public FuzzerConfig minRatio(double minRatio) {
        mainConfig.minRatio(minRatio);
        return this;
    }

    @Override
    synchronized public FuzzerConfig maxRatio(double maxRatio) {
        mainConfig.maxRatio(maxRatio);
        return this;
    }

    @Override
    synchronized public FuzzerConfig startTest(long test) {
        mainConfig.startTest(test);
        return this;
    }

    @Override
    synchronized public FuzzerConfig endTest(long test) {
        mainConfig.endTest(test);
        return this;
    }

    @Override
    synchronized public FuzzerConfig parts(int parts) {
        mainConfig.parts(parts);
        return this;
    }

    @Override
    synchronized public FuzzerConfig readTimeout(long timeout) {
        mainConfig.readTimeout(timeout);
        return this;
    }

    @Override
    synchronized public Config clientCertificate(String path) {
        mainConfig.clientCertificate(path);
        return this;
    }

    @Override
    synchronized public Config clientKey(String path) {
        mainConfig.clientKey(path);
        return this;
    }

    @Override
    synchronized public Config serverCertificate(String path) {
        mainConfig.serverCertificate(path);
        return this;
    }

    @Override
    synchronized public Config serverKey(String path) {
        mainConfig.serverKey(path);
        return this;
    }

    synchronized public FuzzerConfig analyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    synchronized public boolean hasAnalyzer() {
        return analyzer != null;
    }

    synchronized public Analyzer analyzer() {
        return analyzer;
    }

    synchronized public FuzzerConfig set(Check... checks) {
        this.checks = checks;
        return this;
    }

    synchronized public Check[] checks() {
        return checks;
    }

    synchronized public boolean noFactory() {
        return factory == null;
    }

    synchronized public FuzzerConfig factory(StructFactory factory) {
        this.factory = factory;
        return this;
    }

    synchronized public StructFactory factory() {
        return factory;
    }

    synchronized public boolean noClient() {
        return client == null;
    }

    synchronized public FuzzerConfig client(Client client) {
        this.client = client;
        return this;
    }

    synchronized public Client client() {
        return client;
    }

    @Override
    synchronized public boolean equals(Object o) {
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
    synchronized public int hashCode() {
        int result = Objects.hash(mainConfig, analyzer, factory, client);
        result = 31 * result + Arrays.hashCode(checks);
        return result;
    }
}
