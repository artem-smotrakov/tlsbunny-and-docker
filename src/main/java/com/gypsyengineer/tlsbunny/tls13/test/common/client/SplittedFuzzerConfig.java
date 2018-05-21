package com.gypsyengineer.tlsbunny.tls13.test.common.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.Target;
import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;

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
    public Target target() {
        return config.target();
    }

    @Override
    public Mode mode() {
        return config.mode();
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
    public FuzzerConfig target(Target target) {
        return config.target(target);
    }

    @Override
    public FuzzerConfig mode(Mode mode) {
        return config.mode(mode);
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
    public Runnable create() {
        return config.create();
    }
}
