package com.gypsyengineer.tlsbunny.tls13.test.gnutls.client;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.test.MultipleThreads;

public class FuzzyClient {

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(TLSPlaintextFuzzer.configs)
                .add(HandshakeFuzzer.configs)
                .add(ClientHelloFuzzer.configs)
                .add(FinishedFuzzer.configs)
                .add(CCSFuzzer.configs)
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
