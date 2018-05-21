package com.gypsyengineer.tlsbunny.tls13.test.gnutls.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonHandshakeFuzzer;
import com.gypsyengineer.tlsbunny.utils.Output;

public class HandshakeFuzzer extends CommonHandshakeFuzzer {

    public HandshakeFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new HttpsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new FinishedFuzzer(new Output(), config), configs)
                .submit();
    }

}
