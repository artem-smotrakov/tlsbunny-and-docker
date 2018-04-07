package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.tls_plaintext;

public class TLSPlaintextFuzzer extends HandshakeMessageFuzzer {

    static final Config[] configs = new Config[] {
            new TLSPlaintextFuzzerConfig(commonConfig)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
            new TLSPlaintextFuzzerConfig(commonConfig)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
    };

    public TLSPlaintextFuzzer(Output output, TLSPlaintextFuzzerConfig config) {
        super(output, config);
    }

    Engine connect() throws Exception {
        return Engine.init()
                .target(config.host())
                .target(config.port())
                .set(fuzzer)
                .set(output)
                .send(new OutgoingClientHello())
                .send(new OutgoingChangeCipherSpec())
                .expect(new IncomingServerHello())
                .expect(new IncomingChangeCipherSpec())
                .expect(new IncomingEncryptedExtensions())
                .expect(new IncomingCertificate())
                .expect(new IncomingCertificateVerify())
                .expect(new IncomingFinished())
                .send(new OutgoingFinished())
                .allow(new IncomingNewSessionTicket())
                .send(new OutgoingHttpGetRequest())
                .expect(new IncomingApplicationData())
                .connect();
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads().add(configs).submit();
    }

    public static class TLSPlaintextFuzzerConfig extends FuzzerConfig {

        public TLSPlaintextFuzzerConfig(CommonConfig commonConfig) {
            super(commonConfig);
            set(() -> new TLSPlaintextFuzzer(new Output(), this));
            target(tls_plaintext);
        }

    }

}
