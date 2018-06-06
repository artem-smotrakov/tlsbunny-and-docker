package com.gypsyengineer.tlsbunny.tls13.test.gnutls.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.utils.Output;

public class TLSPlaintextFuzzer extends CommonFuzzer {

    public static MultipleThreads.FuzzerFactory factory =
            config -> new TLSPlaintextFuzzer(new Output(), config);

    public TLSPlaintextFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new GnutlsHttpsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(factory, tls_plaintext_configs)
                .submit();
    }

}
