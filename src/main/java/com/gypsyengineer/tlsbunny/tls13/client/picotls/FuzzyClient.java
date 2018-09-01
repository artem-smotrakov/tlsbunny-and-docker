package com.gypsyengineer.tlsbunny.tls13.client.picotls;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.client.common.Runner;

import static com.gypsyengineer.tlsbunny.tls13.client.common.CommonFuzzer.*;

public class FuzzyClient {

    public static void main(String[] args) throws InterruptedException {
        new Runner()
                .add(fuzzerFactory, combine(noClientAuthConfigs(), new PicotlsClient()))
                .set(new NoAlertAnalyzer())
                .submit();
    }
}
