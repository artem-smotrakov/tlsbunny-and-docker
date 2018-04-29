package com.gypsyengineer.tlsbunny.tls13.test;

import com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.Target;

public class FuzzerConfig implements Config {

    public interface FuzzerFactory {
        Runnable create();
    }

    private final CommonConfig commonConfig;
    private FuzzerFactory fuzzerFactory;

    public FuzzerConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    public FuzzerConfig set(FuzzerFactory fuzzerFactory) {
        this.fuzzerFactory = fuzzerFactory;
        return this;
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
    public Config target(Target target) {
        commonConfig.target(target);
        return this;
    }

    @Override
    public Config mode(Mode mode) {
        commonConfig.mode(mode);
        return this;
    }

    @Override
    public Config minRatio(double minRatio) {
        commonConfig.minRatio(minRatio);
        return this;
    }

    @Override
    public Config maxRatio(double maxRatio) {
        commonConfig.maxRatio(maxRatio);
        return this;
    }

    @Override
    public Config startTest(long test) {
        commonConfig.startTest(test);
        return this;
    }

    @Override
    public Config endTest(long test) {
        commonConfig.endTest(test);
        return this;
    }

    @Override
    public Config parts(int parts) {
        commonConfig.parts(parts);
        return this;
    }

    @Override
    public Runnable create() {
        return fuzzerFactory.create();
    }

    @Override
    public FuzzerConfig copy() {
        return new FuzzerConfig(commonConfig.copy()).set(fuzzerFactory);
    }
}
