package com.gypsyengineer.tlsbunny.tls13.test.client;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.*;

public class FuzzyClient {

    private static final Config[] configs = new Config[] {
            new MutatedHttpsConnection.FuzzerConfig()
                    .target(tls_plaintext).mode(byte_flip)
                    .minRatio(0.01).maxRatio(0.09).endTest(10).parts(5),
            new MutatedHttpsConnection.FuzzerConfig()
                    .target(tls_plaintext).mode(bit_flip)
                    .minRatio(0.01).maxRatio(0.09).endTest(10).parts(5),
            new MutatedHttpsConnection.FuzzerConfig()
                    .target(handshake).mode(byte_flip)
                    .minRatio(0.01).maxRatio(0.09).endTest(500).parts(5),
            new MutatedHttpsConnection.FuzzerConfig()
                    .target(handshake).mode(bit_flip)
                    .minRatio(0.01).maxRatio(0.09).endTest(500).parts(5),
            new MutatedHttpsConnection.FuzzerConfig()
                    .target(client_hello).mode(byte_flip)
                    .minRatio(0.01).maxRatio(0.09).endTest(500).parts(5),
            new MutatedHttpsConnection.FuzzerConfig()
                    .target(client_hello).mode(bit_flip)
                    .minRatio(0.01).maxRatio(0.09).endTest(500).parts(5),
            new MutatedHttpsConnection.FuzzerConfig()
                    .target(finished).mode(byte_flip)
                    .minRatio(0.01).maxRatio(0.09).endTest(500).parts(5),
            new MutatedHttpsConnection.FuzzerConfig()
                    .target(finished).mode(bit_flip)
                    .minRatio(0.01).maxRatio(0.09).endTest(500).parts(5),
    };

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads().add(configs).submit();
    }
}
