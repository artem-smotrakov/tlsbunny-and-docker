package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;

import static com.gypsyengineer.tlsbunny.tls13.client.CommonFuzzer.*;

public class FuzzyHttpsClient {

    public static void main(String[] args) throws InterruptedException {
        new Runner()
                .add(fuzzerFactory, combine(noClientAuthConfigs(), new HttpsClient()))
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
