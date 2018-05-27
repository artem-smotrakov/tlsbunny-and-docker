package com.gypsyengineer.tlsbunny.tls13.test.common.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineException;
import com.gypsyengineer.tlsbunny.tls13.connection.SuccessCheck;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.FuzzyStructFactory;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedLegacySessionIdStructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.net.ConnectException;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Fuzzer.Type.mutated_struct_factory;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Fuzzer.Type.semi_mutated_legacy_session_id_struct_factory;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.bit_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.byte_flip;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Mode.mutated_vector;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.*;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.certificate;

public class CommonFuzzer implements Runnable {

    // read timeouts in millis
    public static final long long_read_timeout = 5000;
    public static final long short_read_timeout = 500;

    public static final FuzzerConfig[] tls_plaintext_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(short_read_timeout)
                    .type(mutated_struct_factory)
                    .target(tls_plaintext)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(200)
                    .parts(2),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(short_read_timeout)
                    .type(mutated_struct_factory)
                    .target(tls_plaintext)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(200)
                    .parts(2),
    };

    public static final FuzzerConfig[] ccs_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(long_read_timeout)
                    .type(mutated_struct_factory)
                    .target(ccs)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(20)
                    .parts(1),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(long_read_timeout)
                    .type(mutated_struct_factory)
                    .target(ccs)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(20)
                    .parts(1),
    };

    public static final FuzzerConfig[] handshake_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(short_read_timeout)
                    .type(mutated_struct_factory)
                    .target(handshake)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(2000)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(short_read_timeout)
                    .type(mutated_struct_factory)
                    .target(handshake)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(2000)
                    .parts(5),
    };

    public static final FuzzerConfig[] client_hello_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(long_read_timeout)
                    .type(mutated_struct_factory)
                    .target(client_hello)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(2000)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(long_read_timeout)
                    .type(mutated_struct_factory)
                    .target(client_hello)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(2000)
                    .parts(5),
    };

    public static final FuzzerConfig[] certificate_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(long_read_timeout)
                    .type(mutated_struct_factory)
                    .target(certificate)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(2000)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(long_read_timeout)
                    .type(mutated_struct_factory)
                    .target(certificate)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(2000)
                    .parts(5),
    };

    public static final FuzzerConfig[] certificate_verify_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(long_read_timeout)
                    .type(mutated_struct_factory)
                    .target(certificate_verify)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(2000)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(long_read_timeout)
                    .type(mutated_struct_factory)
                    .target(certificate_verify)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(2000)
                    .parts(5),
    };

    public static final FuzzerConfig[] finished_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(long_read_timeout)
                    .type(mutated_struct_factory)
                    .target(finished)
                    .mode(byte_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(2000)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(long_read_timeout)
                    .type(mutated_struct_factory)
                    .target(finished)
                    .mode(bit_flip)
                    .minRatio(0.01)
                    .maxRatio(0.09)
                    .endTest(2000)
                    .parts(5),
    };

    public static final FuzzerConfig[] legacy_session_id_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .timeout(long_read_timeout)
                    .type(semi_mutated_legacy_session_id_struct_factory)
                    .target(client_hello)
                    .mode(mutated_vector)
                    .parts(1)
    };

    private static final int MAX_ATTEMPTS = 3;
    private static final int DELAY = 3000; // in millis

    protected final Output output;
    protected final FuzzerConfig config;
    protected final FuzzyStructFactory fuzzer;

    protected final Client client;

    public CommonFuzzer(Output output, FuzzerConfig config, Client client) {
        this.output = output;
        this.config = config;
        this.client = client;
        fuzzer = initFuzzer(config);
    }

    private FuzzyStructFactory initFuzzer(FuzzerConfig config) {
        FuzzyStructFactory factory;
        switch (config.type()) {
            case mutated_struct_factory:
                 factory = new MutatedStructFactory(
                        StructFactory.getDefault(),
                        output,
                        config.minRatio(),
                        config.maxRatio()
                );
                factory.setStartTest(config.startTest());
                factory.setEndTest(config.endTest());
                break;
            case semi_mutated_legacy_session_id_struct_factory:
                factory = new MutatedLegacySessionIdStructFactory(
                        StructFactory.getDefault(),
                        output);
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("hey! unknown fuzzer type: %s", config.type()));
        }

        factory.target(config.target());
        factory.mode(config.mode());

        return factory;
    }

    @Override
    public void run() {
        try {
            output.info("run a smoke test before fuzzing");
            client.connect(config, StructFactory.getDefault()).run(new SuccessCheck());
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
