package com.gypsyengineer.tlsbunny.tls13.test.nss.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.tls13.test.gnutls.client.HttpsClient;
import com.gypsyengineer.tlsbunny.utils.Output;

public class FinishedFuzzer extends CommonFuzzer {

    public FinishedFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new HttpsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new FinishedFuzzer(new Output(), config), finished_configs)
                .submit();
    }

}
