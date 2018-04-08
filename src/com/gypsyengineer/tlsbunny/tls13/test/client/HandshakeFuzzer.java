package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.handshake;

public class HandshakeFuzzer extends HandshakeMessageFuzzer {

    static final Config[] configs = new Config[] {
            new HandshakeFuzzerConfig(commonConfig)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
            new HandshakeFuzzerConfig(commonConfig)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
    };

    public HandshakeFuzzer(Output output, HandshakeFuzzerConfig config) {
        super(output, config);
    }

    @Override
    Engine connect(StructFactory factory) throws Exception {
        return Engine.init()
                .target(config.host())
                .target(config.port())
                .set(factory)
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

    public static class HandshakeFuzzerConfig extends FuzzerConfig {

        public HandshakeFuzzerConfig(CommonConfig commonConfig) {
            super(commonConfig);
            set(() -> new HandshakeFuzzer(new Output(), this));
            target(handshake);
        }

    }

}
