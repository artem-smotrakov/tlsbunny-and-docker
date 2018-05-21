package com.gypsyengineer.tlsbunny.tls13.test.nss.client;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;

public class FuzzyClient {

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(TLSPlaintextFuzzer.factory, TLSPlaintextFuzzer.tls_plaintext_configs)
                .add(CCSFuzzer.factory, CCSFuzzer.ccs_configs)
                .add(HandshakeFuzzer.factory, HandshakeFuzzer.handshake_configs)
                .add(ClientHelloFuzzer.factory, ClientHelloFuzzer.client_hello_configs)
                .add(FinishedFuzzer.factory, FinishedFuzzer.finished_configs)
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
