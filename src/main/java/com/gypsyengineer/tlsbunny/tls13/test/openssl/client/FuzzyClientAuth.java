package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.test.MultipleThreads;
import com.gypsyengineer.tlsbunny.utils.Output;

public class FuzzyClientAuth {

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new CertificateFuzzer(new Output(), config), CertificateFuzzer.configs)
                .add(config -> new CertificateVerifyFuzzer(new Output(), config), CertificateVerifyFuzzer.configs)
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
