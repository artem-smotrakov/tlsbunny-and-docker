package com.gypsyengineer.tlsbunny.tls13.test.picotls.client;

import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonFuzzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.tls13.test.openssl.client.OpensslHttpsClient;
import com.gypsyengineer.tlsbunny.utils.Output;

public class LegacySessionIdFuzzer extends CommonFuzzer {

    public static MultipleThreads.FuzzerFactory factory =
            config -> new LegacySessionIdFuzzer(new Output(), config);

    public LegacySessionIdFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new OpensslHttpsClient());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(factory, legacy_session_id_configs)
                .submit();
    }

}
