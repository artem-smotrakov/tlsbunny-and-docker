package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.fuzzer.Ratio;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gypsyengineer.tlsbunny.fuzzer.BitFlipFuzzer.newBitFlipFuzzer;
import static com.gypsyengineer.tlsbunny.fuzzer.ByteFlipFuzzer.newByteFlipFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.CipherSuitesFuzzer.cipherSuitesFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.ExtensionVectorFuzzer.newExtensionVectorFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.LegacyCompressionMethodsFuzzer.newLegacyCompressionMethodsFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.LegacySessionIdFuzzer.newLegacySessionIdFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory.newMutatedStructFactory;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.SimpleVectorFuzzer.simpleVectorFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.*;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.client_hello;

public class Configs {

    public static final int TLS_PLAINTEXT_HEADER_LENGTH =
            ContentType.ENCODING_LENGTH + ProtocolVersion.ENCODING_LENGTH
                    + UInt16.ENCODING_LENGTH - 1;

    public static final int HANDSHAKE_HEADER_LENGTH =
            HandshakeType.ENCODING_LENGTH + UInt24.ENCODING_LENGTH - 1;

    // read timeouts in millis
    public static final long long_read_timeout = 5000;
    public static final long short_read_timeout = 500;

    private static final Ratio[] byte_flip_ratios = {
            new Ratio(0.01, 0.02),
            new Ratio(0.02, 0.03),
            new Ratio(0.03, 0.04),
            new Ratio(0.04, 0.05),
            new Ratio(0.05, 0.06),
            new Ratio(0.06, 0.07),
            new Ratio(0.07, 0.08),
            new Ratio(0.08, 0.09),
            new Ratio(0.1, 0.2),
            new Ratio(0.2, 0.3),
            new Ratio(0.3, 0.4),
            new Ratio(0.4, 0.5),
            new Ratio(0.5, 0.6),
            new Ratio(0.6, 0.7),
            new Ratio(0.7, 0.8),
            new Ratio(0.8, 0.9),
            new Ratio(0.9, 1.0),
    };

    private static final Ratio[] bit_flip_ratios = {
            new Ratio(0.01, 0.02),
            new Ratio(0.02, 0.03),
            new Ratio(0.03, 0.04),
            new Ratio(0.04, 0.05),
            new Ratio(0.05, 0.06),
            new Ratio(0.06, 0.07),
            new Ratio(0.07, 0.08),
            new Ratio(0.08, 0.09),
    };

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

    public static FuzzerConfig[] clientHelloConfigs(Config config) {
        return merge(
                enumerateByteFlipRatios(
                        () -> newMutatedStructFactory().target(client_hello),
                        new FuzzerConfig(config)
                                .readTimeout(long_read_timeout)
                                .endTest(2000)
                                .parts(5)),
                enumerateBitFlipRatios(
                        () -> newMutatedStructFactory().target(client_hello),
                        new FuzzerConfig(config)
                                .readTimeout(long_read_timeout)
                                .endTest(2000)
                                .parts(5)));
    }

    public static FuzzerConfig[] certificateConfigs(Config config) {
        return merge(
                enumerateByteFlipRatios(
                        () -> newMutatedStructFactory().target(certificate),
                        new FuzzerConfig(config)
                                .readTimeout(long_read_timeout)
                                .endTest(2000)
                                .parts(5)),
                enumerateBitFlipRatios(
                        () -> newMutatedStructFactory().target(certificate),
                        new FuzzerConfig(config)
                                .readTimeout(long_read_timeout)
                                .endTest(2000)
                                .parts(5)));
    }

    public static FuzzerConfig[] certificateVerifyConfigs(Config config) {
        return merge(
                enumerateByteFlipRatios(
                        () -> newMutatedStructFactory().target(certificate_verify),
                        new FuzzerConfig(config)
                                .readTimeout(long_read_timeout)
                                .endTest(2000)
                                .parts(5)),
                enumerateBitFlipRatios(
                        () -> newMutatedStructFactory().target(certificate_verify),
                        new FuzzerConfig(config)
                                .readTimeout(long_read_timeout)
                                .endTest(2000)
                                .parts(5)));
    }

    public static FuzzerConfig[] finishedConfigs(Config config) {
        return merge(
                enumerateByteFlipRatios(
                        () -> newMutatedStructFactory().target(finished),
                        new FuzzerConfig(config)
                                .readTimeout(long_read_timeout)
                                .endTest(2000)
                                .parts(5)),
                enumerateBitFlipRatios(
                        () -> newMutatedStructFactory().target(finished),
                        new FuzzerConfig(config)
                                .readTimeout(long_read_timeout)
                                .endTest(2000)
                                .parts(5)));
    }

    public static FuzzerConfig[] cipherSuitesConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(cipherSuitesFuzzer()
                                .target(client_hello)
                                .fuzzer(simpleVectorFuzzer()))
                        .readTimeout(long_read_timeout)
        };
    }

    public static FuzzerConfig[] extensionVectorConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(newExtensionVectorFuzzer()
                                .target(client_hello)
                                .fuzzer(simpleVectorFuzzer()))
                        .readTimeout(long_read_timeout)
        };
    }

    public static FuzzerConfig[] legacySessionIdConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(newLegacySessionIdFuzzer()
                                .target(client_hello)
                                .fuzzer(simpleVectorFuzzer()))
                        .readTimeout(long_read_timeout)
        };
    }

    public static FuzzerConfig[] legacyCompressionMethodsConfigs(Config config) {
        return new FuzzerConfig[] {
                new FuzzerConfig(config)
                        .factory(newLegacyCompressionMethodsFuzzer()
                                .target(client_hello)
                                .fuzzer(simpleVectorFuzzer()))
                        .readTimeout(long_read_timeout)
        };
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

    public static FuzzerConfig[] clientAuthConfigs(Config config) {
        List<FuzzerConfig> configs = new ArrayList<>();
        configs.addAll(Arrays.asList(certificateConfigs(config)));
        configs.addAll(Arrays.asList(certificateVerifyConfigs(config)));

        return configs.toArray(new FuzzerConfig[configs.size()]);
    }

    public static FuzzerConfig[] allConfigs(Config config) {
        List<FuzzerConfig> configs = new ArrayList<>();
        configs.addAll(Arrays.asList(noClientAuthConfigs(config)));
        configs.addAll(Arrays.asList(clientAuthConfigs(config)));

        return configs.toArray(new FuzzerConfig[configs.size()]);
    }

    public static FuzzerConfig[] allConfigs() {
        return allConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] noClientAuthConfigs() {
        return noClientAuthConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] clientAuthConfigs() {
        return clientAuthConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] ccsConfigs() {
        return ccsConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] tlsPlaintextConfigs() {
        return tlsPlaintextConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] handshakeConfigs() {
        return handshakeConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] clientHelloConfigs() {
        return clientHelloConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] certificateConfigs() {
        return certificateConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] certificateVerifyConfigs() {
        return certificateVerifyConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] finishedConfigs() {
        return finishedConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] cipherSuitesConfigs() {
        return cipherSuitesConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] extensionVectorConfigs() {
        return extensionVectorConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] legacySessionIdConfigs() {
        return legacySessionIdConfigs(SystemPropertiesConfig.load());
    }

    public static FuzzerConfig[] legacyCompressionMethodsConfigs() {
        return legacyCompressionMethodsConfigs(SystemPropertiesConfig.load());
    }

    // helper methods

    private static FuzzerConfig[] enumerateByteFlipRatios(
            FuzzyStructFactoryBuilder builder, FuzzerConfig... configs) {

        List<FuzzerConfig> generatedConfigs = new ArrayList<>();
        for (FuzzerConfig config : configs) {
            for (Ratio ratio : byte_flip_ratios) {
                FuzzerConfig newConfig = config.copy();
                FuzzyStructFactory fuzzyStructFactory = builder.build();
                fuzzyStructFactory.fuzzer(newByteFlipFuzzer()
                        .minRatio(ratio.min())
                        .maxRatio(ratio.max()));
                newConfig.factory(fuzzyStructFactory);

                generatedConfigs.add(newConfig);
            }
        }

        return generatedConfigs.toArray(
                new FuzzerConfig[generatedConfigs.size()]);
    }

    private static FuzzerConfig[] enumerateBitFlipRatios(
            FuzzyStructFactoryBuilder builder, FuzzerConfig... configs) {

        List<FuzzerConfig> generatedConfigs = new ArrayList<>();
        for (FuzzerConfig config : configs) {
            for (Ratio ratio : bit_flip_ratios) {
                FuzzerConfig newConfig = config.copy();
                FuzzyStructFactory fuzzyStructFactory = builder.build();
                fuzzyStructFactory.fuzzer(newBitFlipFuzzer()
                        .minRatio(ratio.min())
                        .maxRatio(ratio.max()));
                newConfig.factory(fuzzyStructFactory);

                generatedConfigs.add(newConfig);
            }
        }

        return generatedConfigs.toArray(
                new FuzzerConfig[generatedConfigs.size()]);
    }

    private static FuzzerConfig[] merge(FuzzerConfig[]... lists) {
        List<FuzzerConfig> result = new ArrayList<>();
        for (FuzzerConfig[] configs : lists) {
            result.addAll(List.of(configs));
        }
        return result.toArray(new FuzzerConfig[result.size()]);
    }

    private interface FuzzyStructFactoryBuilder {
        FuzzyStructFactory build();
    }
}
