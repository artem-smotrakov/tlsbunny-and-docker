package com.gypsyengineer.tlsbunny.tls13.test;

import com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.Target;

public interface Config {
    String host();
    int port();
    Target target();
    Mode mode();
    double minRatio();
    double maxRatio();
    int threads();
    int parts();
    long startTest();
    long endTest();
    String clientCertificate();
    String clientKey();

    Config target(Target target);
    Config mode(Mode mode);
    Config minRatio(double minRatio);
    Config maxRatio(double maxRatio);
    Config startTest(long test);
    Config endTest(long test);
    Config parts(int parts);

    Runnable create();
    Config copy();
}
