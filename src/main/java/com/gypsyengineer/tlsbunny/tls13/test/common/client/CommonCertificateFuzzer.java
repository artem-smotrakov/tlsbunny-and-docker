package com.gypsyengineer.tlsbunny.tls13.test.common.client;

import com.gypsyengineer.tlsbunny.tls13.fuzzer.Target;
import com.gypsyengineer.tlsbunny.tls13.test.CommonConfig;
import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.certificate;

public class CommonCertificateFuzzer extends CommonFuzzer {

    public static final FuzzerConfig[] configs = new FuzzerConfig[] {
            new FuzzerConfig(CommonConfig.load())
                    .target(certificate)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
            new FuzzerConfig(CommonConfig.load())
                    .target(certificate)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
    };

    public CommonCertificateFuzzer(Output output, FuzzerConfig config, Client client) {
        super(output, config, client);
    }

}
