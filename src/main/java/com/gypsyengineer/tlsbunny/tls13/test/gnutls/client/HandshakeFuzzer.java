package com.gypsyengineer.tlsbunny.tls13.test.gnutls.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.utils.Output;

public class HandshakeFuzzer extends CommonFuzzer {

    public HandshakeFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new HttpsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new HandshakeFuzzer(new Output(), config), handshake_configs)
                .submit();
    }

}
