package com.gypsyengineer.tlsbunny.tls13.test;

public interface Config {
    String host();
    int port();
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

    Config minRatio(double minRatio);
    Config maxRatio(double maxRatio);
    Config startTest(long test);
    Config endTest(long test);
    Config parts(int parts);
    Config readTimeout(long timeout);
}
