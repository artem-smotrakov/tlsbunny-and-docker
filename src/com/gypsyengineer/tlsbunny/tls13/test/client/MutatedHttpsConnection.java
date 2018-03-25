package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;

public class MutatedHttpsConnection implements Runnable {

    public static final String HTTP_GET_REQUEST = "GET / HTTP/1.1\n\n";

    private final Output output;
    private final Config config;
    private final MutatedStructFactory fuzzer;

    MutatedHttpsConnection(
            MutatedStructFactory fuzzer, Output output, Config config) {

        this.output = output;
        this.config = config;
        this.fuzzer = fuzzer;
    }

    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();

        int threads = config.getThreads();

        if (threads > 1) {
            MultipleThreads.submit(
                    config.getStartTest(),
                    config.getTotal(),
                    config.getParts(),
                    threads,
                    (test, limit) -> create(config));
        } else {
            create(config).run();
        }
    }

    @Override
    public void run() {
        try {
            String threadName = Thread.currentThread().getName();
            while (fuzzer.canFuzz()) {
                output.info("%s, test %d of %d",
                        threadName, fuzzer.getTest(), config.getTotal());
                output.info("now fuzzer's state is '%s'", fuzzer.getState());
                try {
                    TLSConnection.create()
                            .target(config.getHost())
                            .target(config.getPort())
                            .set(fuzzer)
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
                            .send(new OutgoingApplicationData(HTTP_GET_REQUEST))
                            .expect(new IncomingApplicationData())
                            .run();
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

    private static MutatedHttpsConnection create(Config config) {
        Output output = new Output();

        MutatedStructFactory fuzzer = new MutatedStructFactory(
                StructFactory.getDefault(),
                output,
                config.getMinRatio(),
                config.getMaxRatio()
        );
        fuzzer.setTarget(config.getTarget());

        return new MutatedHttpsConnection(fuzzer, output, config);
    }

}
