package com.gypsyengineer.tlsbunny.tls13.test.gnutls.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.client_hello;

public class ClientHelloFuzzer extends HandshakeMessageFuzzer {

    static final FuzzerConfig[] configs = new FuzzerConfig[] {
            new ClientHelloFuzzerConfig(CommonConfig.load())
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
            new ClientHelloFuzzerConfig(CommonConfig.load())
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
    };

    public ClientHelloFuzzer(Output output, ClientHelloFuzzerConfig config) {
        super(output, config);
    }

    @Override
    protected Engine connect(StructFactory factory) throws Exception {
        return HttpsClient.go(config, factory);
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads().add(configs).submit();
    }

    public static class ClientHelloFuzzerConfig extends FuzzerConfig {

        public ClientHelloFuzzerConfig(Config commonConfig) {
            super(commonConfig);
            target(client_hello);
        }

        @Override
        public Runnable create() {
            return new ClientHelloFuzzer(new Output(), this);
        }

    }

}
