package com.gypsyengineer.tlsbunny.tls13.test.nss.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.MultipleThreads;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonCCSFuzzer;
import com.gypsyengineer.tlsbunny.tls13.test.gnutls.client.HttpsClient;
import com.gypsyengineer.tlsbunny.utils.Output;

public class CCSFuzzer extends CommonCCSFuzzer {

    public CCSFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new HttpsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new ClientHelloFuzzer(new Output(), config), configs)
                .submit();
    }

}
