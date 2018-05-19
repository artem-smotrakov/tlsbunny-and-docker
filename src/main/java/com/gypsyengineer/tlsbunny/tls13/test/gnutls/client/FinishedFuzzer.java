package com.gypsyengineer.tlsbunny.tls13.test.gnutls.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.finished;

public class FinishedFuzzer extends HandshakeMessageFuzzer {

    static final FuzzerConfig[] configs = new FuzzerConfig[] {
            new FinishedFuzzerConfig(CommonConfig.load())
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(1000)
                    .parts(5),
            new FinishedFuzzerConfig(CommonConfig.load())
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(1000)
                    .parts(5),
    };

    public FinishedFuzzer(Output output, FinishedFuzzerConfig config) {
        super(output, config);
    }

    @Override
    protected Engine connect(StructFactory factory) throws Exception {
        return HttpsClient.go(config, factory).apply(config.analyzer());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads().add(configs).submit();
    }

    public static class FinishedFuzzerConfig extends FuzzerConfig {

        public FinishedFuzzerConfig(Config commonConfig) {
            super(commonConfig);
            target(finished);
        }

        @Override
        public Runnable create() {
            return new FinishedFuzzer(new Output(), this);
        }

    }

}
