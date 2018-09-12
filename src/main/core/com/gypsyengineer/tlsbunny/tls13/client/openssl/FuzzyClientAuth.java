package com.gypsyengineer.tlsbunny.tls13.client.openssl;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.client.common.Runner;

import static com.gypsyengineer.tlsbunny.tls13.client.common.CommonFuzzer.clientAuthConfigs;
import static com.gypsyengineer.tlsbunny.tls13.client.common.CommonFuzzer.combine;
import static com.gypsyengineer.tlsbunny.tls13.client.common.CommonFuzzer.fuzzerFactory;

public class FuzzyClientAuth {

    public static void main(String[] args) throws InterruptedException {
        new Runner()
                .add(fuzzerFactory, combine(clientAuthConfigs(), new OpensslHttpsClient()))
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
