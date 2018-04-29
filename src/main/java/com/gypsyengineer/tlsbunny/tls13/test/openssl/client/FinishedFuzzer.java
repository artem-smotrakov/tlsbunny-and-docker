package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.*;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.finished;

public class FinishedFuzzer extends HandshakeMessageFuzzer {

    private static final CommonConfig commonConfig = CommonConfig.load();

    static final Config[] configs = new Config[] {
            new FinishedFuzzerConfig(commonConfig)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(1000)
                    .parts(5),
            new FinishedFuzzerConfig(commonConfig)
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
        return Engine.init()
                .target(config.host())
                .target(config.port())
                .set(factory)
                .set(output)
                .send(new OutgoingClientHello())
                .send(new OutgoingChangeCipherSpec())
                .require(new IncomingServerHello())
                .require(new IncomingChangeCipherSpec())
                .require(new IncomingEncryptedExtensions())
                .require(new IncomingCertificate())
                .require(new IncomingCertificateVerify())
                .require(new IncomingFinished())
                .send(new OutgoingFinished())
                .allow(new IncomingNewSessionTicket())
                .send(new OutgoingHttpGetRequest())
                .require(new IncomingApplicationData())
                .connect();
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads().add(configs).submit();
    }

    public static class FinishedFuzzerConfig extends FuzzerConfig {

        public FinishedFuzzerConfig(CommonConfig commonConfig) {
            super(commonConfig);
            factory(() -> new FinishedFuzzer(new Output(), this));
            target(finished);
        }

    }

}
