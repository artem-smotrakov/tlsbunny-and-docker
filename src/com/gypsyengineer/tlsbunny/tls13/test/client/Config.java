package com.gypsyengineer.tlsbunny.tls13.test.client;

public interface Config {
    String host();
    int port();
    double minRatio();
    double maxRatio();
    int threads();
    int parts();
    long startTest();
    long endTest();

    Config minRatio(double minRatio);
    Config maxRatio(double maxRatio);
    Config startTest(long test);
    Config endTest(long test);
    Config parts(int parts);

    Runnable create();
    Config copy();
}
