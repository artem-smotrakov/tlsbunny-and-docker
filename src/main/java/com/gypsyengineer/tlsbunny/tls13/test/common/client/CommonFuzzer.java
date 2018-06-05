package com.gypsyengineer.tlsbunny.tls13.test.common.client;

import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineException;
import com.gypsyengineer.tlsbunny.tls13.connection.SuccessCheck;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.FuzzyStructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.test.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.net.ConnectException;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.BitFlipFuzzer.newBitFlipFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.ByteFlipFuzzer.newByteFlipFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.LegacySessionIdFuzzer.newMutatedLegacySessionIdStructFactory;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory.newMutatedStructFactory;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.SimpleByteVectorFuzzer.newSimpleByteVectorFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.*;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.certificate;

public class CommonFuzzer implements Runnable {

    public static final int TLS_PLAINTEXT_HEADER_LENGTH =
            ContentType.ENCODING_LENGTH + ProtocolVersion.ENCODING_LENGTH
                    + UInt16.ENCODING_LENGTH - 1;

    public static final int HANDSHAKE_HEADER_LENGTH =
            HandshakeType.ENCODING_LENGTH + UInt24.ENCODING_LENGTH - 1;

    // read timeouts in millis
    public static final long long_read_timeout = 5000;
    public static final long short_read_timeout = 500;

    public static final FuzzerConfig[] tls_plaintext_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(tls_plaintext)
                            .fuzzer(newByteFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)
                                    .startIndex(0)
                                    .endIndex(TLS_PLAINTEXT_HEADER_LENGTH)))
                    .readTimeout(short_read_timeout)
                    .endTest(200)
                    .parts(2),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(tls_plaintext)
                            .fuzzer(newBitFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)
                                    .startIndex(0)
                                    .endIndex(TLS_PLAINTEXT_HEADER_LENGTH)))
                    .readTimeout(short_read_timeout)
                    .endTest(200)
                    .parts(2),
    };

    public static final FuzzerConfig[] ccs_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(ccs)
                            .fuzzer(newByteFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)))
                    .readTimeout(long_read_timeout)
                    .endTest(20)
                    .parts(1),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(ccs)
                            .fuzzer(newBitFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)))
                    .readTimeout(long_read_timeout)
                    .endTest(20)
                    .parts(1),
    };

    public static final FuzzerConfig[] handshake_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(handshake)
                            .fuzzer(newByteFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)
                                    .startIndex(0)
                                    .endIndex(HANDSHAKE_HEADER_LENGTH)))
                    .readTimeout(short_read_timeout)
                    .endTest(2000)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(handshake)
                            .fuzzer(newBitFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)
                                    .startIndex(0)
                                    .endIndex(HANDSHAKE_HEADER_LENGTH)))
                    .readTimeout(short_read_timeout)
                    .endTest(2000)
                    .parts(5),
    };

    public static final FuzzerConfig[] client_hello_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(client_hello)
                            .fuzzer(newByteFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)))
                    .readTimeout(long_read_timeout)
                    .endTest(2000)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(client_hello)
                            .fuzzer(newBitFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)))
                    .readTimeout(long_read_timeout)
                    .endTest(2000)
                    .parts(5),
    };

    public static final FuzzerConfig[] certificate_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(certificate)
                            .fuzzer(newByteFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)))
                    .readTimeout(long_read_timeout)
                    .endTest(2000)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(certificate)
                            .fuzzer(newBitFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)))
                    .readTimeout(long_read_timeout)
                    .endTest(2000)
                    .parts(5),
    };

    public static final FuzzerConfig[] certificate_verify_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(certificate_verify)
                            .fuzzer(newByteFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)))
                    .readTimeout(long_read_timeout)
                    .endTest(2000)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(certificate_verify)
                            .fuzzer(newBitFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)))
                    .readTimeout(long_read_timeout)
                    .endTest(2000)
                    .parts(5),
    };

    public static final FuzzerConfig[] finished_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(finished)
                            .fuzzer(newByteFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)))
                    .readTimeout(long_read_timeout)
                    .endTest(2000)
                    .parts(5),
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedStructFactory()
                            .target(finished)
                            .fuzzer(newBitFlipFuzzer()
                                    .minRatio(0.01)
                                    .maxRatio(0.09)))
                    .readTimeout(long_read_timeout)
                    .endTest(2000)
                    .parts(5),
    };

    public static final FuzzerConfig[] legacy_session_id_configs = new FuzzerConfig[] {
            new FuzzerConfig(SystemPropertiesConfig.load())
                    .factory(newMutatedLegacySessionIdStructFactory()
                            .target(client_hello)
                            .fuzzer(newSimpleByteVectorFuzzer()))
                    .readTimeout(long_read_timeout)
                    .parts(1)
    };

    private static final int MAX_ATTEMPTS = 3;
    private static final int DELAY = 3000; // in millis

    protected final Output output;
    protected final FuzzerConfig config;

    protected final Client client;

    public CommonFuzzer(Output output, FuzzerConfig config, Client client) {
        this.output = output;
        this.config = config;
        this.client = client;

        config.factory().setOutput(output);
    }

    @Override
    public void run() {
        FuzzyStructFactory fuzzer = config.factory();

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
