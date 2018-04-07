package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.Target;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;

public class FuzzyCertificate implements Runnable {

    public static final String HTTP_GET_REQUEST = "GET / HTTP/1.1\n\n";

    private final Output output;
    private final FuzzerConfig config;
    private final MutatedStructFactory fuzzer;

    FuzzyCertificate(Output output, FuzzerConfig config) {
        fuzzer = new MutatedStructFactory(
                StructFactory.getDefault(),
                output,
                config.minRatio(),
                config.maxRatio()
        );
        fuzzer.setTarget(Target.certificate);
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
                            .send(new OutgoingApplicationData(HTTP_GET_REQUEST))
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

    // TODO: duplicate
    public static class FuzzerConfig implements Config {

        public static final String DEFAULT_TARGET = "tls_plaintext";
        public static final String DEFAULT_MODE = "byte_flip";

        private CommonConfig commonConfig;
        private String target = System.getProperty("tlsbunny.target", DEFAULT_TARGET).trim();
        private String mode = System.getProperty("tlsbunny.mode", DEFAULT_MODE).trim();

        public FuzzerConfig() {
            this(new CommonConfig());
        }

        public FuzzerConfig(CommonConfig commonConfig) {
            this.commonConfig = commonConfig;
        }

        public FuzzyCertificate create() {
            return new FuzzyCertificate(new Output(), this);
        }

        public FuzzerConfig target(String target) {
            this.target = target;
            return this;
        }

        public FuzzerConfig mode(String mode) {
            this.mode = mode;
            return this;
        }

        public String target() {
            return target;
        }

        public String mode() {
            return mode;
        }

        @Override
        public String host() {
            return commonConfig.host();
        }

        @Override
        public int port() {
            return commonConfig.port();
        }

        @Override
        public double minRatio() {
            return commonConfig.minRatio();
        }

        @Override
        public double maxRatio() {
            return commonConfig.maxRatio();
        }

        @Override
        public int threads() {
            return commonConfig.threads();
        }

        @Override
        public int parts() {
            return commonConfig.parts();
        }

        @Override
        public long startTest() {
            return commonConfig.startTest();
        }

        @Override
        public long endTest() {
            return commonConfig.endTest();
        }

        @Override
        public String clientCertificate() {
            return commonConfig.clientCertificate();
        }

        @Override
        public String clientKey() {
            return commonConfig.clientKey();
        }

        @Override
        public Config minRatio(double ratio) {
            commonConfig.minRatio(ratio);
            return this;
        }

        @Override
        public Config maxRatio(double ratio) {
            commonConfig.maxRatio(ratio);
            return this;
        }

        @Override
        public Config startTest(long test) {
            commonConfig.startTest(test);
            return this;
        }

        @Override
        public Config endTest(long test) {
            commonConfig.endTest(test);
            return this;
        }

        @Override
        public Config parts(int parts) {
            commonConfig.parts(parts);
            return this;
        }

        @Override
        public Config copy() {
            FuzzerConfig clone = new FuzzerConfig(commonConfig.copy());
            clone.target(target);
            clone.mode(mode);

            return clone;
        }
    }

}
