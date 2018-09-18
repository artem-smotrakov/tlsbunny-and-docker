package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.client.Runner;

import static com.gypsyengineer.tlsbunny.tls13.client.CommonFuzzer.clientAuthConfigs;
import static com.gypsyengineer.tlsbunny.tls13.client.CommonFuzzer.combine;
import static com.gypsyengineer.tlsbunny.tls13.client.CommonFuzzer.fuzzerFactory;

public class FuzzyClientAuth {

    public static void main(String[] args) throws InterruptedException {
        new Runner()
                .add(fuzzerFactory, combine(clientAuthConfigs(), new HttpsClient()))
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
