package com.gypsyengineer.tlsbunny.tls13.test.picotls.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.utils.Output;

public class ClientHelloFuzzer extends CommonFuzzer {

    public static MultipleThreads.FuzzerFactory factory =
            config -> new ClientHelloFuzzer(new Output(), config);

    public ClientHelloFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new PicotlsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(factory, client_hello_configs)
                .submit();
    }

}
