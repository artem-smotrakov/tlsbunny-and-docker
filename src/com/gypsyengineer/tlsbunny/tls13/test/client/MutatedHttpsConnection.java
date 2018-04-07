package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;

public class MutatedHttpsConnection implements Runnable {

    private final Output output;
    private final FuzzerConfig config;
    private final MutatedStructFactory fuzzer;

    MutatedHttpsConnection(Output output, FuzzerConfig config) {
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
                            .expect(new IncomingServerHello())
                            .expect(new IncomingChangeCipherSpec())
                            .expect(new IncomingEncryptedExtensions())
                            .expect(new IncomingCertificate())
                            .expect(new IncomingCertificateVerify())
                            .expect(new IncomingFinished())
                            .send(new OutgoingFinished())
                            .allow(new IncomingChangeCipherSpec())
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
        FuzzerConfig config = new FuzzerConfig();

        int threads = config.threads();

        if (threads > 1) {
            new MultipleThreads().add(config).submit();
        } else {
            config.create().run();
        }
    }

    public static class FuzzerConfig extends CommonConfig {

        public MutatedHttpsConnection create() {
            return new MutatedHttpsConnection(new Output(), this);
        }

    }

}
