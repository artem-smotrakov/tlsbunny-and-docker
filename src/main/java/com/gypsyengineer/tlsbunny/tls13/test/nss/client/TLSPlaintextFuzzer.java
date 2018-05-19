package com.gypsyengineer.tlsbunny.tls13.test.nss.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.tls_plaintext;

public class TLSPlaintextFuzzer extends HandshakeMessageFuzzer {

    static final FuzzerConfig[] configs = new FuzzerConfig[] {
            new TLSPlaintextFuzzerConfig(CommonConfig.load())
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(200)
                    .parts(1),
            new TLSPlaintextFuzzerConfig(CommonConfig.load())
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(200)
                    .parts(1),
    };

    public TLSPlaintextFuzzer(Output output, TLSPlaintextFuzzerConfig config) {
        super(output, config);
    }

    @Override
    protected Engine connect(StructFactory factory) throws Exception {
        return HttpsClient.go(config, factory).apply(config.analyzer());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads().add(configs).submit();
    }

    public static class TLSPlaintextFuzzerConfig extends FuzzerConfig {

        public TLSPlaintextFuzzerConfig(Config commonConfig) {
            super(commonConfig);
            target(tls_plaintext);
        }

        @Override
        public Runnable create() {
            return new TLSPlaintextFuzzer(new Output(), this);
        }

    }

}
