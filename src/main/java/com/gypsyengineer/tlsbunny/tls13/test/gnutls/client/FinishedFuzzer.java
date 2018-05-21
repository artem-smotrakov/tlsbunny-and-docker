package com.gypsyengineer.tlsbunny.tls13.test.gnutls.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFinishedFuzzer;
import com.gypsyengineer.tlsbunny.utils.Output;

public class FinishedFuzzer extends CommonFinishedFuzzer {

    public FinishedFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new HttpsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new FinishedFuzzer(new Output(), config), configs)
                .submit();
    }

}
