package com.gypsyengineer.tlsbunny.tls13.test.common.client;

import com.gypsyengineer.tlsbunny.tls13.test.CommonConfig;
import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.tls_plaintext;

public class CommonTLSPlaintextFuzzer extends CommonFuzzer {

    public static final FuzzerConfig[] configs = new FuzzerConfig[] {
            new FuzzerConfig(CommonConfig.load())
                    .target(tls_plaintext)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(200)
                    .parts(1),
            new FuzzerConfig(CommonConfig.load())
                    .target(tls_plaintext)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(200)
                    .parts(1),
    };

    public CommonTLSPlaintextFuzzer(Output output, FuzzerConfig config, Client client) {
        super(output, config, client);
    }

}
