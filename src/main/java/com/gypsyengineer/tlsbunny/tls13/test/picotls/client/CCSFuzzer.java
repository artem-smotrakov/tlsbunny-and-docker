package com.gypsyengineer.tlsbunny.tls13.test.picotls.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.tls13.test.gnutls.client.HttpsClient;
import com.gypsyengineer.tlsbunny.utils.Output;

public class CCSFuzzer extends CommonFuzzer {

    public CCSFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new HttpsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new ClientHelloFuzzer(new Output(), config), ccs_configs)
                .submit();
    }

}
