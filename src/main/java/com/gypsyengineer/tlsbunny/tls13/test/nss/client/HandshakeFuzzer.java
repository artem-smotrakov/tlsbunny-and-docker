package com.gypsyengineer.tlsbunny.tls13.test.nss.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.utils.Output;

public class HandshakeFuzzer extends CommonFuzzer {

    public static MultipleThreads.FuzzerFactory factory =
            config -> new HandshakeFuzzer(new Output(), config);

    public HandshakeFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new NssHttpsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(factory, handshake_configs)
                .submit();
    }

}
