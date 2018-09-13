package com.gypsyengineer.tlsbunny.impl.test.tls13.old.picotls;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.client.Runner;

import static com.gypsyengineer.tlsbunny.tls13.client.CommonFuzzer.*;

public class FuzzyClient {

    public static void main(String[] args) throws InterruptedException {
        new Runner()
                .add(fuzzerFactory, combine(noClientAuthConfigs(), new PicotlsClient()))
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
