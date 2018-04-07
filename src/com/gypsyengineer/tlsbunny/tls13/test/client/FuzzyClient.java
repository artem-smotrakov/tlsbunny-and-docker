package com.gypsyengineer.tlsbunny.tls13.test.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FuzzyClient {

    private static final List<Config> configs = new ArrayList<>();
    static {
        configs.addAll(Arrays.asList(TLSPlaintextFuzzer.configs));
        configs.addAll(Arrays.asList(HandshakeFuzzer.configs));
        configs.addAll(Arrays.asList(ClientHelloFuzzer.configs));
        configs.addAll(Arrays.asList(FinishedFuzzer.configs));
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads().add(configs).submit();
    }
}
