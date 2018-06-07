package com.gypsyengineer.tlsbunny.tls13.test.nss.client;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;

import static com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer.*;

public class FuzzyClient {

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(factory, combine(noClientAuthConfigs(), new NssHttpsClient()))
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
