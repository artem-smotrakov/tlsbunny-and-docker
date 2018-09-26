package com.gypsyengineer.tlsbunny.utils;

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
    String serverCertificate();
    String serverKey();
    String targetFilter();

    // timeout for reading incoming data (in millis)
    long readTimeout();

    Config host(String host);
    Config port(int port);
    Config minRatio(double minRatio);
    Config maxRatio(double maxRatio);
    Config startTest(long test);
    Config endTest(long test);
    Config parts(int parts);
    Config readTimeout(long timeout);
    Config clientCertificate(String path);
    Config clientKey(String path);
    Config serverCertificate(String path);
    Config serverKey(String path);
}
