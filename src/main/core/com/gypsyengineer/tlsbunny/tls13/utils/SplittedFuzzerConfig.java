package com.gypsyengineer.tlsbunny.tls13.utils;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;

public class SplittedFuzzerConfig extends FuzzerConfig {

    public static final int ALWAYS_ONE_PART = 1;

    private final FuzzerConfig config;
    private final long startTest;
    private final long endTest;

    public SplittedFuzzerConfig(FuzzerConfig config, long startTest, long endTest) {
        super(config);
        this.config = config;
        this.startTest = startTest;
        this.endTest = endTest;
    }

    @Override
    public String host() {
        return config.host();
    }

    @Override
    public int port() {
        return config.port();
    }

    @Override
    public double minRatio() {
        return config.minRatio();
    }

    @Override
    public double maxRatio() {
        return config.maxRatio();
    }

    @Override
    public int threads() {
        return config.threads();
    }

    @Override
    public int parts() {
        return ALWAYS_ONE_PART;
    }

    @Override
    public long startTest() {
        return startTest;
    }

    @Override
    public long endTest() {
        return endTest;
    }

    @Override
    public String clientCertificate() {
        return config.clientCertificate();
    }

    @Override
    public String clientKey() {
        return config.clientKey();
    }

    @Override
    public FuzzerConfig minRatio(double minRatio) {
        return config.minRatio(minRatio);
    }

    @Override
    public FuzzerConfig maxRatio(double maxRatio) {
        return config.maxRatio(maxRatio);
    }

    @Override
    public FuzzerConfig startTest(long test) {
        throw new UnsupportedOperationException("what the hell? I can't set start test!");
    }

    @Override
    public FuzzerConfig endTest(long test) {
        throw new UnsupportedOperationException("what the hell? I can't set end test!");
    }

    @Override
    public FuzzerConfig parts(int parts) {
        throw new UnsupportedOperationException("what the hell? I can't set parts!");
    }

    @Override
    public FuzzerConfig analyzer(Analyzer analyzer) {
        return config.analyzer(analyzer);
    }

    @Override
    public boolean hasAnalyzer() {
        return config.hasAnalyzer();
    }

    @Override
    public Analyzer analyzer() {
        return config.analyzer();
    }

    @Override
    public long readTimeout() {
        return config.readTimeout();
    }

    @Override
    public FuzzerConfig readTimeout(long timeout) {
        return config.readTimeout(timeout);
    }

    @Override
    public FuzzerConfig factory(StructFactory factory) {
        return config.factory(factory);
    }

    @Override
    public StructFactory factory() {
        return config.factory();
    }

    @Override
    public boolean noFactory() {
        return config.noFactory();
    }

    @Override
    public boolean noClient() {
        return config.noClient();
    }

    @Override
    public FuzzerConfig client(Client client) {
        return config.client(client);
    }

    @Override
    public Client client() {
        return config.client();
    }

    @Override
    public FuzzerConfig set(Config mainConfig) {
        return config.set(mainConfig);
    }

    @Override
    public String serverCertificate() {
        return config.serverCertificate();
    }

    @Override
    public String serverKey() {
        return config.serverKey();
    }

    @Override
    public String targetFilter() {
        return config.targetFilter();
    }

    @Override
    public Config host(String host) {
        return config.host(host);
    }

    @Override
    public Config port(int port) {
        return config.port(port);
    }

    @Override
    public Config clientCertificate(String path) {
        return config.clientCertificate(path);
    }

    @Override
    public Config clientKey(String path) {
        return config.clientKey(path);
    }

    @Override
    public Config serverCertificate(String path) {
        return config.serverCertificate(path);
    }

    @Override
    public Config serverKey(String path) {
        return config.serverKey(path);
    }
}
