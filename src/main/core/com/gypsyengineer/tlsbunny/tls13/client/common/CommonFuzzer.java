package com.gypsyengineer.tlsbunny.tls13.client.common;

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
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.BitFlipFuzzer.newBitFlipFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.ByteFlipFuzzer.newByteFlipFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.CipherSuitesFuzzer.newCipherSuitesFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.ExtensionVectorFuzzer.newExtensionVectorFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.LegacyCompressionMethodsFuzzer.newLegacyCompressionMethodsFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.LegacySessionIdFuzzer.newLegacySessionIdFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory.newMutatedStructFactory;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.SimpleVectorFuzzer.newSimpleVectorFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.*;

public class CommonFuzzer implements Runnable {

    public static final int TLS_PLAINTEXT_HEADER_LENGTH =
            ContentType.ENCODING_LENGTH + ProtocolVersion.ENCODING_LENGTH
                    + UInt16.ENCODING_LENGTH - 1;

    public static final int HANDSHAKE_HEADER_LENGTH =
            HandshakeType.ENCODING_LENGTH + UInt24.ENCODING_LENGTH - 1;

    // read timeouts in millis
    public static final long long_read_timeout = 5000;
    public static final long short_read_timeout = 500;

    public static FuzzerConfig[] tlsPlaintextConfigs() {
        return new FuzzerConfig[] {
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
    }

    public static FuzzerConfig[] ccsConfigs() {
        return new FuzzerConfig[] {
                new FuzzerConfig(SystemPropertiesConfig.load())
                        .factory(newMutatedStructFactory()
                                .target(ccs)
                                .fuzzer(newByteFlipFuzzer()
                                        .minRatio(0.01)
                                        .maxRatio(0.09)))
                        .readTimeout(long_read_timeout)
                        .endTest(20),
                new FuzzerConfig(SystemPropertiesConfig.load())
                        .factory(newMutatedStructFactory()
                                .target(ccs)
                                .fuzzer(newBitFlipFuzzer()
                                        .minRatio(0.01)
                                        .maxRatio(0.09)))
                        .readTimeout(long_read_timeout)
                        .endTest(20),
        };
    }

    public static FuzzerConfig[] handshakeConfigs() {
        return new FuzzerConfig[] {
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
    }

    public static FuzzerConfig[] clientHelloConfigs() {
        return new FuzzerConfig[] {
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
    }

    public static FuzzerConfig[] certificateConfigs() {
        return new FuzzerConfig[] {
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
    }

    public static FuzzerConfig[] certificateVerifyConfigs() {
        return new FuzzerConfig[] {
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
    }

    public static FuzzerConfig[] finishedConfigs() {
        return new FuzzerConfig[] {
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
    }

    public static FuzzerConfig[] cipherSuitesConfigs() {
        return new FuzzerConfig[] {
                new FuzzerConfig(SystemPropertiesConfig.load())
                        .factory(newCipherSuitesFuzzer()
                                .target(client_hello)
                                .fuzzer(newSimpleVectorFuzzer()))
                        .readTimeout(long_read_timeout)
        };
    }

    public static FuzzerConfig[] extensionVectorConfigs() {
        return new FuzzerConfig[] {
                new FuzzerConfig(SystemPropertiesConfig.load())
                        .factory(newExtensionVectorFuzzer()
                                .target(client_hello)
                                .fuzzer(newSimpleVectorFuzzer()))
                        .readTimeout(long_read_timeout)
        };
    }

    public static FuzzerConfig[] legacySessionIdConfigs() {
        return new FuzzerConfig[] {
                new FuzzerConfig(SystemPropertiesConfig.load())
                        .factory(newLegacySessionIdFuzzer()
                                .target(client_hello)
                                .fuzzer(newSimpleVectorFuzzer()))
                        .readTimeout(long_read_timeout)
        };
    }

    public static FuzzerConfig[] legacyCompressionMethodsConfigs() {
        return new FuzzerConfig[] {
                new FuzzerConfig(SystemPropertiesConfig.load())
                        .factory(newLegacyCompressionMethodsFuzzer()
                                .target(client_hello)
                                .fuzzer(newSimpleVectorFuzzer()))
                        .readTimeout(long_read_timeout)
        };
    }

    public static FuzzerConfig[] noClientAuthConfigs() {
        List<FuzzerConfig> configs = new ArrayList<>();
        configs.addAll(Arrays.asList(tlsPlaintextConfigs()));
        configs.addAll(Arrays.asList(ccsConfigs()));
        configs.addAll(Arrays.asList(handshakeConfigs()));
        configs.addAll(Arrays.asList(clientHelloConfigs()));
        configs.addAll(Arrays.asList(finishedConfigs()));
        configs.addAll(Arrays.asList(cipherSuitesConfigs()));
        configs.addAll(Arrays.asList(extensionVectorConfigs()));
        configs.addAll(Arrays.asList(legacySessionIdConfigs()));
        configs.addAll(Arrays.asList(legacyCompressionMethodsConfigs()));

        return configs.toArray(new FuzzerConfig[configs.size()]);
    }

    public static FuzzerConfig[] clientAuthConfigs() {
        List<FuzzerConfig> configs = new ArrayList<>();
        configs.addAll(Arrays.asList(certificateConfigs()));
        configs.addAll(Arrays.asList(certificateVerifyConfigs()));

        return configs.toArray(new FuzzerConfig[configs.size()]);
    }

    public static FuzzerConfig[] allConfigs() {
        List<FuzzerConfig> configs = new ArrayList<>();
        configs.addAll(Arrays.asList(noClientAuthConfigs()));
        configs.addAll(Arrays.asList(clientAuthConfigs()));

        return configs.toArray(new FuzzerConfig[configs.size()]);
    }

    public static Runner.FuzzerFactory fuzzerFactory =
            (config, output) -> new CommonFuzzer(output, config);

    private static final int MAX_ATTEMPTS = 3;
    private static final int DELAY = 3000; // in millis

    protected final Output output;
    protected final FuzzerConfig config;

    public CommonFuzzer(Output output, FuzzerConfig config) {
        this.output = output;
        this.config = config;
    }

    @Override
    public void run() {
        if (config.noFactory()) {
            throw new IllegalArgumentException(
                    "what the hell? no fuzzy set specified!");
        }
        FuzzyStructFactory fuzzer = config.factory();

        if (config.noClient()) {
            throw new IllegalArgumentException("what the hell? no client specified");
        }
        Client client = config.client();

        fuzzer.setOutput(output);

        try {
            output.info("run a smoke test before fuzzing");
            client.set(StructFactory.getDefault())
                    .set(config)
                    .set(output)
                    .connect()
                    .run(new SuccessCheck());
        } catch (Exception e) {
            output.achtung("smoke test failed: %s", e.getMessage());
            output.achtung("skip fuzzing");
            return;
        } finally {
            output.flush();
        }

        output.info("smoke test passed, start fuzzing");
        try {
            while (fuzzer.canFuzz()) {
                output.info("test %d", fuzzer.getTest());
                output.info("now fuzzer's state is '%s'", fuzzer.getState());

                int attempt = 0;
                while (true) {
                    try {
                        Engine engine = client.set(fuzzer).set(config).set(output)
                                .connect();

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

    public static FuzzerConfig[] combine(FuzzerConfig[] configs, Client client) {
        for (FuzzerConfig config : configs) {
            config.client(client);
        }

        return configs;
    }

}
