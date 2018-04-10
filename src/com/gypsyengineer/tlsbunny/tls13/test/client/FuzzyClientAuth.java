package com.gypsyengineer.tlsbunny.tls13.test.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FuzzyClientAuth {

    private static final List<Config> configs = new ArrayList<>();
    static {
        configs.addAll(Arrays.asList(CertificateFuzzer.configs));
        configs.addAll(Arrays.asList(CertificateVerifyFuzzer.configs));
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads().add(configs).submit();
    }
}
