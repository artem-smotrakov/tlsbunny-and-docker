package com.gypsyengineer.tlsbunny.tls13.test.wolfssl.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.utils.Output;

public class CCSFuzzer extends CommonFuzzer {

    public static MultipleThreads.FuzzerFactory factory =
            config -> new CCSFuzzer(new Output(), config);

    public CCSFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new WolfsslHttpsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(factory, ccs_configs)
                .submit();
    }

}
