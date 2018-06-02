package com.gypsyengineer.tlsbunny.tls13.test;

import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.Fuzzer;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.Target;

public class FuzzerConfig implements Config {

    private final Config commonConfig;
    private Fuzzer.Type type;
    private Analyzer analyzer;

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
    public Mode mode() {
        return commonConfig.mode();
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
    public FuzzerConfig mode(Mode mode) {
        commonConfig.mode(mode);
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

    public FuzzerConfig type(Fuzzer.Type type) {
        this.type = type;
        return this;
    }

    public Fuzzer.Type type() {
        return type;
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

    public Runnable create() {
        throw new UnsupportedOperationException("what the hell? I can't create a fuzzer!");
    }

}
