package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.Runner;

import static com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer.clientAuthConfigs;
import static com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer.combine;
import static com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer.factory;

public class FuzzyClientAuth {

    public static void main(String[] args) throws InterruptedException {
        new Runner()
                .add(factory, combine(clientAuthConfigs(), new OpensslHttpsClient()))
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
