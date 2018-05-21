package com.gypsyengineer.tlsbunny.tls13.test.h2o.client;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.utils.Output;

public class FuzzyClient {

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new TLSPlaintextFuzzer(new Output(), config), TLSPlaintextFuzzer.tls_plaintext_configs)
                .add(config -> new HandshakeFuzzer(new Output(), config), HandshakeFuzzer.handshake_configs)
                .add(config -> new ClientHelloFuzzer(new Output(), config), ClientHelloFuzzer.client_hello_configs)
                .add(config -> new FinishedFuzzer(new Output(), config), FinishedFuzzer.finished_configs)
                .add(config -> new CCSFuzzer(new Output(), config), CCSFuzzer.ccs_configs)
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
