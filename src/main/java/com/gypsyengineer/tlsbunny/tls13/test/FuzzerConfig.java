package com.gypsyengineer.tlsbunny.tls13.test;

import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.FuzzyStructFactory;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.Target;

public class FuzzerConfig implements Config {

    private final Config commonConfig;
    private Analyzer analyzer;
    private FuzzyStructFactory factory;

    public FuzzerConfig(Config commonConfig) {
        this.commonConfig = commonConfig;
    }

    @Override
    public String host() {
        return commonConfig.host();
    }

    @Override
    public int port() {
        return commonConfig.port();
    }

    @Override
    public Target target() {
        return commonConfig.target();
    }

    @Override
    public double minRatio() {
        return commonConfig.minRatio();
    }

    @Override
    public double maxRatio() {
        return commonConfig.maxRatio();
    }

    @Override
    public int threads() {
        return commonConfig.threads();
    }

    @Override
    public int parts() {
        return commonConfig.parts();
    }

    @Override
    public long startTest() {
        return commonConfig.startTest();
    }

    @Override
    public long endTest() {
        return commonConfig.endTest();
    }

    @Override
    public String clientCertificate() {
        return commonConfig.clientCertificate();
    }

    @Override
    public String clientKey() {
        return commonConfig.clientKey();
    }

    @Override
    public long readTimeout() {
        return commonConfig.readTimeout();
    }

    @Override
    public FuzzerConfig target(Target target) {
        commonConfig.target(target);
        return this;
    }

    @Override
    public FuzzerConfig minRatio(double minRatio) {
        commonConfig.minRatio(minRatio);
        return this;
    }

    @Override
    public FuzzerConfig maxRatio(double maxRatio) {
        commonConfig.maxRatio(maxRatio);
        return this;
    }

    @Override
    public FuzzerConfig startTest(long test) {
        commonConfig.startTest(test);
        return this;
    }

    @Override
    public FuzzerConfig endTest(long test) {
        commonConfig.endTest(test);
        return this;
    }

    @Override
    public FuzzerConfig parts(int parts) {
        commonConfig.parts(parts);
        return this;
    }

    @Override
    public FuzzerConfig readTimeout(long timeout) {
        commonConfig.readTimeout(timeout);
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

    public FuzzerConfig factory(FuzzyStructFactory factory) {
        this.factory = factory;
        return this;
    }

    public FuzzyStructFactory factory() {
        return factory;
    }

}
