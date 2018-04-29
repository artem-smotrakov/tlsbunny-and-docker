package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.test.MultipleThreads;

public class FuzzyClientAuth {

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(CertificateFuzzer.configs)
                .add(CertificateVerifyFuzzer.configs)
                .submit();
    }
}
