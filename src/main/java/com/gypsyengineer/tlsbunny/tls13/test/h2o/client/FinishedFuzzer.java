package com.gypsyengineer.tlsbunny.tls13.test.h2o.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.utils.Output;

public class FinishedFuzzer extends CommonFuzzer {

    public static MultipleThreads.FuzzerFactory factory =
            config -> new FinishedFuzzer(new Output(), config);

    public FinishedFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new H2oHttpsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(factory, finished_configs)
                .submit();
    }

}
