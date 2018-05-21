package com.gypsyengineer.tlsbunny.tls13.test.picotls.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.MultipleThreads;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonTLSPlaintextFuzzer;
import com.gypsyengineer.tlsbunny.tls13.test.gnutls.client.HttpsClient;
import com.gypsyengineer.tlsbunny.utils.Output;

public class TLSPlaintextFuzzer extends CommonTLSPlaintextFuzzer {

    public TLSPlaintextFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new HttpsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new FinishedFuzzer(new Output(), config), configs)
                .submit();
    }

}
