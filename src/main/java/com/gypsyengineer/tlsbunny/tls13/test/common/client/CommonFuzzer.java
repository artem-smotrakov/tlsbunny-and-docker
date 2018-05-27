package com.gypsyengineer.tlsbunny.tls13.test.common.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineException;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.net.ConnectException;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.*;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.certificate;

public class CommonFuzzer implements Runnable {

    // read timeouts in millis
    public static final long LONG_READ_TIMEOUT = 5000;
    public static final long SHORT_READ_TIMEOUT = 500;

    public static final FuzzerConfig[] tls_plaintext_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(SHORT_READ_TIMEOUT)
                    .target(tls_plaintext)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(200)
                    .parts(1),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(SHORT_READ_TIMEOUT)
                    .target(tls_plaintext)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(200)
                    .parts(1),
    };

    public static final FuzzerConfig[] ccs_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(LONG_READ_TIMEOUT)
                    .target(ccs)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(LONG_READ_TIMEOUT)
                    .target(ccs)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
    };

    public static final FuzzerConfig[] handshake_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(SHORT_READ_TIMEOUT)
                    .target(handshake)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(1000)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(SHORT_READ_TIMEOUT)
                    .target(handshake)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(1000)
                    .parts(5),
    };

    public static final FuzzerConfig[] client_hello_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(LONG_READ_TIMEOUT)
                    .target(client_hello)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(LONG_READ_TIMEOUT)
                    .target(client_hello)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
    };

    public static final FuzzerConfig[] certificate_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(LONG_READ_TIMEOUT)
                    .target(certificate)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(LONG_READ_TIMEOUT)
                    .target(certificate)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
    };

    public static final FuzzerConfig[] certificate_verify_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(LONG_READ_TIMEOUT)
                    .target(certificate_verify)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(LONG_READ_TIMEOUT)
                    .target(certificate_verify)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(10)
                    .parts(5),
    };

    public static final FuzzerConfig[] finished_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(LONG_READ_TIMEOUT)
                    .target(finished)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(1000)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(LONG_READ_TIMEOUT)
                    .target(finished)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(1000)
                    .parts(5),
    };

    private static final int MAX_ATTEMPTS = 3;
    private static final int DELAY = 3000; // in millis

    protected final Output output;
    protected final FuzzerConfig config;
    protected final MutatedStructFactory fuzzer;

    protected final Client client;

    public CommonFuzzer(Output output, FuzzerConfig config, Client client) {
        fuzzer = new MutatedStructFactory(
                StructFactory.getDefault(),
                output,
                config.minRatio(),
                config.maxRatio()
        );
        fuzzer.target(config.target());
        fuzzer.mode(config.mode());
        fuzzer.setStartTest(config.startTest());
        fuzzer.setEndTest(config.endTest());

        this.output = output;
        this.config = config;

        this.client = client;
    }

    @Override
    public void run() {
        try {
            output.info("run a smoke test before fuzzing");
            client.connect(config, StructFactory.getDefault()).run(new NoAlertCheck());
        } catch (Exception e) {
            output.achtung("smoke test failed: %s", e.getMessage());
            output.achtung("skip fuzzing");
            return;
        } finally {
            output.flush();
        }

        output.info("smoke test passed, start fuzzing");
        try {
            output.prefix(Thread.currentThread().getName());
            while (fuzzer.canFuzz()) {
                output.info("test %d", fuzzer.getTest());
                output.info("now fuzzer's state is '%s'", fuzzer.getState());

                int attempt = 0;
                while (true) {
                    try {
                        Engine engine = client.connect(config, fuzzer);

                        if (config.hasAnalyzer()) {
                            engine.apply(config.analyzer());
                        }

                        break;
                    } catch (EngineException e) {
                        Throwable cause = e.getCause();
                        if (cause instanceof ConnectException == false) {
                            throw e;
                        }

                        if (attempt == MAX_ATTEMPTS) {
                            throw new IOException("looks like the server closed connection");
                        }
                        attempt++;

                        output.info("connection failed: %s ", cause.getMessage());
                        output.info("let's wait a bit and try again (attempt %d)", attempt);
                        Thread.sleep(DELAY);

                        continue;
                    }
                }

                output.flush();
                fuzzer.moveOn();
            }
        } catch (Exception e) {
            output.achtung("what the hell? unexpected exception", e);
        } finally {
            output.flush();
        }
    }

}
