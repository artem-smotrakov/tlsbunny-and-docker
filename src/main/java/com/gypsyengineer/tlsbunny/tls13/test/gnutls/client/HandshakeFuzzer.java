package com.gypsyengineer.tlsbunny.tls13.test.gnutls.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.handshake;

public class HandshakeFuzzer extends HandshakeMessageFuzzer {

    static final FuzzerConfig[] configs = new FuzzerConfig[] {
            new HandshakeFuzzerConfig(CommonConfig.load())
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(1000)
                    .parts(5),
            new HandshakeFuzzerConfig(CommonConfig.load())
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(1000)
                    .parts(5),
    };

    public HandshakeFuzzer(Output output, HandshakeFuzzerConfig config) {
        super(output, config);
    }

    @Override
    protected Engine connect(StructFactory factory) throws Exception {
        return HttpsClient.go(config, factory).apply(config.analyzer());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads().add(configs).submit();
    }

    public static class HandshakeFuzzerConfig extends FuzzerConfig {

        public HandshakeFuzzerConfig(Config commonConfig) {
            super(commonConfig);
            target(handshake);
        }

        @Override
        public Runnable create() {
            return new HandshakeFuzzer(new Output(), this);
        }

    }

}
