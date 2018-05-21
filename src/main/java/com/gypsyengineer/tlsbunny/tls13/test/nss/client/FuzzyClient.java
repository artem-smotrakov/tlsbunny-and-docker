package com.gypsyengineer.tlsbunny.tls13.test.nss.client;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.test.MultipleThreads;
import com.gypsyengineer.tlsbunny.utils.Output;

public class FuzzyClient {

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new TLSPlaintextFuzzer(new Output(), config), TLSPlaintextFuzzer.configs)
                .add(config -> new HandshakeFuzzer(new Output(), config), HandshakeFuzzer.configs)
                .add(config -> new ClientHelloFuzzer(new Output(), config), ClientHelloFuzzer.configs)
                .add(config -> new FinishedFuzzer(new Output(), config), FinishedFuzzer.configs)
                .add(config -> new CCSFuzzer(new Output(), config), CCSFuzzer.configs)
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
