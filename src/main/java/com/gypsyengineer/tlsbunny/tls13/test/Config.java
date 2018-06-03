package com.gypsyengineer.tlsbunny.tls13.test;

import com.gypsyengineer.tlsbunny.tls13.fuzzer.Target;

public interface Config {
    String host();
    int port();
    Target target();
    double minRatio();
    double maxRatio();
    int threads();
    int parts();
    long startTest();
    long endTest();
    String clientCertificate();
    String clientKey();

    // timeout for reading incoming data (in millis)
    long readTimeout();

    Config target(Target target);
    Config minRatio(double minRatio);
    Config maxRatio(double maxRatio);
    Config startTest(long test);
    Config endTest(long test);
    Config parts(int parts);
    Config readTimeout(long timeout);
}
