package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.certificate;

public class FuzzyCertificate implements Runnable {

    private static final CommonConfig commonConfig = new CommonConfig();

    private static final Config[] configs = new Config[] {
            new CertificateFuzzerConfig(commonConfig)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
            new CertificateFuzzerConfig(commonConfig)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
    };

    private final Output output;
    private final CertificateFuzzerConfig config;
    private final MutatedStructFactory fuzzer;

    public FuzzyCertificate(Output output, CertificateFuzzerConfig config) {
        fuzzer = new MutatedStructFactory(
                StructFactory.getDefault(),
                output,
                config.minRatio(),
                config.maxRatio()
        );
        fuzzer.setTarget(config.target());
        fuzzer.setMode(config.mode());
        fuzzer.setStartTest(config.startTest());
        fuzzer.setEndTest(config.endTest());

        this.output = output;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            output.prefix(Thread.currentThread().getName());
            while (fuzzer.canFuzz()) {
                output.info("test %d", fuzzer.getTest());
                output.info("now fuzzer's state is '%s'", fuzzer.getState());
                try {
                    Engine.init()
                            .target(config.host())
                            .target(config.port())
                            .set(fuzzer)
                            .set(output)
                            .send(new OutgoingClientHello())
                            .send(new OutgoingChangeCipherSpec())
                            .expect(new IncomingServerHello())
                            .expect(new IncomingChangeCipherSpec())
                            .expect(new IncomingEncryptedExtensions())
                            .expect(new IncomingCertificateRequest())
                            .expect(new IncomingCertificate())
                            .expect(new IncomingCertificateVerify())
                            .expect(new IncomingFinished())
                            .send(new OutgoingCertificate()
                                    .certificate(config.clientCertificate()))
                            .send(new OutgoingCertificateVerify()
                                    .key(config.clientKey()))
                            .send(new OutgoingFinished())
                            .allow(new IncomingNewSessionTicket())
                            .send(new OutgoingHttpGetRequest())
                            .expect(new IncomingApplicationData())
                            .connect();
                } finally {
                    output.flush();
                    fuzzer.moveOn();
                }
            }
        } catch (IOException e) {
            output.info("looks like the server closed connection", e);
        } catch (Exception e) {
            output.achtung("what the hell? unexpected exception", e);
        } finally {
            output.flush();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads().add(configs).submit();
    }

    public static class CertificateFuzzerConfig extends FuzzerConfig {

        public CertificateFuzzerConfig(CommonConfig commonConfig) {
            super(commonConfig);
            set(() -> new FuzzyCertificate(new Output(), this));
            target(certificate);
        }

    }

}
