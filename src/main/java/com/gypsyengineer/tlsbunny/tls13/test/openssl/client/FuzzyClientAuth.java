package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.utils.Output;

public class FuzzyClientAuth {

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new CertificateFuzzer(new Output(), config), CertificateFuzzer.certificate_configs)
                .add(config -> new CertificateVerifyFuzzer(new Output(), config), CertificateVerifyFuzzer.certificate_verify_configs)
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
