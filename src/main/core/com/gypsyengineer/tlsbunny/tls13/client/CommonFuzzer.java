package com.gypsyengineer.tlsbunny.tls13.client;

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
import com.gypsyengineer.tlsbunny.utils.Config;
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

    public static Runner.FuzzerFactory fuzzerFactory =
            (config, output) -> new CommonFuzzer(output, config);

    public static final int TLS_PLAINTEXT_HEADER_LENGTH =
            ContentType.ENCODING_LENGTH + ProtocolVersion.ENCODING_LENGTH
                    + UInt16.ENCODING_LENGTH - 1;

    public static final int HANDSHAKE_HEADER_LENGTH =
            HandshakeType.ENCODING_LENGTH + UInt24.ENCODING_LENGTH - 1;

    // read timeouts in millis
    public static final long long_read_timeout = 5000;
    public static final long short_read_timeout = 500;

    private static final int max_attempts = 3;
    private static final int delay = 3000; // in millis

    protected final Output output;
    protected final FuzzerConfig config;

    protected boolean strict = true;
    protected boolean smokeTest = true;

    public static FuzzerConfig[] tlsPlaintextConfigs() {
        return tlsPlaintextConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] tlsPlaintextConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
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
                new FuzzerConfig(config)
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
        return ccsConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] ccsConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(newMutatedStructFactory()
                                .target(ccs)
                                .fuzzer(newByteFlipFuzzer()
                                        .minRatio(0.01)
                                        .maxRatio(0.09)))
                        .readTimeout(long_read_timeout)
                        .endTest(20),
                new FuzzerConfig(config)
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
        return handshakeConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] handshakeConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
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
                new FuzzerConfig(config)
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
        return clientHelloConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] clientHelloConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(newMutatedStructFactory()
                                .target(client_hello)
                                .fuzzer(newByteFlipFuzzer()
                                        .minRatio(0.01)
                                        .maxRatio(0.09)))
                        .readTimeout(long_read_timeout)
                        .endTest(2000)
                        .parts(5),
                new FuzzerConfig(config)
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
        return certificateConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] certificateConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(newMutatedStructFactory()
                                .target(certificate)
                                .fuzzer(newByteFlipFuzzer()
                                        .minRatio(0.01)
                                        .maxRatio(0.09)))
                        .readTimeout(long_read_timeout)
                        .endTest(2000)
                        .parts(5),
                new FuzzerConfig(config)
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
        return certificateVerifyConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] certificateVerifyConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(newMutatedStructFactory()
                                .target(certificate_verify)
                                .fuzzer(newByteFlipFuzzer()
                                        .minRatio(0.01)
                                        .maxRatio(0.09)))
                        .readTimeout(long_read_timeout)
                        .endTest(2000)
                        .parts(5),
                new FuzzerConfig(config)
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
        return finishedConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] finishedConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(newMutatedStructFactory()
                                .target(finished)
                                .fuzzer(newByteFlipFuzzer()
                                        .minRatio(0.01)
                                        .maxRatio(0.09)))
                        .readTimeout(long_read_timeout)
                        .endTest(2000)
                        .parts(5),
                new FuzzerConfig(config)
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
        return cipherSuitesConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] cipherSuitesConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(newCipherSuitesFuzzer()
                                .target(client_hello)
                                .fuzzer(newSimpleVectorFuzzer()))
                        .readTimeout(long_read_timeout)
        };
    }

    public static FuzzerConfig[] extensionVectorConfigs() {
        return extensionVectorConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] extensionVectorConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(newExtensionVectorFuzzer()
                                .target(client_hello)
                                .fuzzer(newSimpleVectorFuzzer()))
                        .readTimeout(long_read_timeout)
        };
    }

    public static FuzzerConfig[] legacySessionIdConfigs() {
        return legacySessionIdConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] legacySessionIdConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(newLegacySessionIdFuzzer()
                                .target(client_hello)
                                .fuzzer(newSimpleVectorFuzzer()))
                        .readTimeout(long_read_timeout)
        };
    }

    public static FuzzerConfig[] legacyCompressionMethodsConfigs() {
        return legacyCompressionMethodsConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] legacyCompressionMethodsConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(newLegacyCompressionMethodsFuzzer()
                                .target(client_hello)
                                .fuzzer(newSimpleVectorFuzzer()))
                        .readTimeout(long_read_timeout)
        };
    }

    public static FuzzerConfig[] noClientAuthConfigs() {
        return noClientAuthConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] noClientAuthConfigs(Config config) {
        List<FuzzerConfig> configs = new ArrayList<>();
        configs.addAll(Arrays.asList(tlsPlaintextConfigs(config)));
        configs.addAll(Arrays.asList(ccsConfigs(config)));
        configs.addAll(Arrays.asList(handshakeConfigs(config)));
        configs.addAll(Arrays.asList(clientHelloConfigs(config)));
        configs.addAll(Arrays.asList(finishedConfigs(config)));
        configs.addAll(Arrays.asList(cipherSuitesConfigs(config)));
        configs.addAll(Arrays.asList(extensionVectorConfigs(config)));
        configs.addAll(Arrays.asList(legacySessionIdConfigs(config)));
        configs.addAll(Arrays.asList(legacyCompressionMethodsConfigs(config)));

        return configs.toArray(new FuzzerConfig[configs.size()]);
    }

    public static FuzzerConfig[] clientAuthConfigs() {
        return clientAuthConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] clientAuthConfigs(Config config) {
        List<FuzzerConfig> configs = new ArrayList<>();
        configs.addAll(Arrays.asList(certificateConfigs(config)));
        configs.addAll(Arrays.asList(certificateVerifyConfigs(config)));

        return configs.toArray(new FuzzerConfig[configs.size()]);
    }

    public static FuzzerConfig[] allConfigs() {
        return allConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] allConfigs(Config config) {
        List<FuzzerConfig> configs = new ArrayList<>();
        configs.addAll(Arrays.asList(noClientAuthConfigs(config)));
        configs.addAll(Arrays.asList(clientAuthConfigs(config)));

        return configs.toArray(new FuzzerConfig[configs.size()]);
    }

    public CommonFuzzer(Output output, FuzzerConfig config) {
        this.output = output;
        this.config = config;
    }

    public CommonFuzzer noSmokeTest() {
        smokeTest = false;
        return this;
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

        if (smokeTest) {
            try {
                output.info("run a smoke test before fuzzing");
                client.set(StructFactory.getDefault())
                        .set(config)
                        .set(output)
                        .connect()
                        .engine()
                        .run(new SuccessCheck());
            } catch (Exception e) {
                reportError("smoke test failed", e);
                output.achtung("skip fuzzing");
                return;
            } finally {
                output.flush();
            }
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
                                .connect().engine();

                        if (config.hasAnalyzer()) {
                            engine.apply(config.analyzer());
                        }

                        break;
                    } catch (EngineException e) {
                        Throwable cause = e.getCause();
                        if (cause instanceof ConnectException == false) {
                            throw e;
                        }

                        if (attempt == max_attempts) {
                            throw new IOException("looks like the server closed connection");
                        }
                        attempt++;

                        output.info("connection failed: %s ", cause.getMessage());
                        output.info("let's wait a bit and try again (attempt %d)", attempt);
                        Thread.sleep(delay);

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

    protected void reportError(String message, Throwable e) {
        if (strict) {
            throw new RuntimeException(message, e);
        }

        output.achtung(message, e);
    }

    public static FuzzerConfig[] combine(FuzzerConfig[] configs, Client client) {
        for (FuzzerConfig config : configs) {
            config.client(client);
        }

        return configs;
    }

}
