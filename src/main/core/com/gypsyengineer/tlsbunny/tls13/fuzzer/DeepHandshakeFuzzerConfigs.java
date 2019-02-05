package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.fuzzer.Ratio;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;

import java.util.ArrayList;
import java.util.List;

import static com.gypsyengineer.tlsbunny.fuzzer.BitFlipFuzzer.newBitFlipFuzzer;
import static com.gypsyengineer.tlsbunny.fuzzer.ByteFlipFuzzer.newByteFlipFuzzer;

public class DeepHandshakeFuzzerConfigs {

    // settings for minimized configs
    private static boolean fullConfigs = Boolean.valueOf(
            System.getProperty("tlsbunny.fuzzer.full.configs", "false"));
    private static final int total = 3;
    private static final int parts = 1;

    private static final long long_read_timeout = 5000;

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

    public static FuzzerConfig[] noClientAuth(Config config) {
        return minimizeIfNecessary(
                concatenate(
                    enumerateByteFlipRatios(
                            DeepHandshakeFuzzer::deepHandshakeFuzzer,
                            new FuzzerConfig(config)
                                    .readTimeout(long_read_timeout)
                                    .total(2000)
                                    .parts(5)),
                    enumerateBitFlipRatios(
                            DeepHandshakeFuzzer::deepHandshakeFuzzer,
                            new FuzzerConfig(config)
                                    .readTimeout(long_read_timeout)
                                    .total(2000)
                                    .parts(5))
                )
        );
    }

    public static FuzzerConfig[] clientAuth(Config config) {
        return minimizeIfNecessary(
                concatenate(
                    enumerateByteFlipRatios(
                            DeepHandshakeFuzzer::deepHandshakeFuzzer,
                            new FuzzerConfig(config)
                                    .readTimeout(long_read_timeout)
                                    .total(2000)
                                    .parts(5)),
                    enumerateBitFlipRatios(
                            DeepHandshakeFuzzer::deepHandshakeFuzzer,
                            new FuzzerConfig(config)
                                    .readTimeout(long_read_timeout)
                                    .total(2000)
                                    .parts(5))
                )
        );
    }

    // helper methods

    public static FuzzerConfig[] minimizeIfNecessary(FuzzerConfig... configs) {
        if (fullConfigs) {
            return configs;
        }

        for (FuzzerConfig config : configs) {
            config.total(total);
            config.parts(parts);
        }

        return configs;
    }

    private static FuzzerConfig[] enumerateByteFlipRatios(
            FuzzyStructFactoryBuilder builder, FuzzerConfig config) {

        // don't enumerate if a state is set
        if (config.hasState()) {
            FuzzerConfig newConfig = config.copy();
            DeepHandshakeFuzzer deepHandshakeFuzzer = builder.build();
            deepHandshakeFuzzer.fuzzer(newByteFlipFuzzer());
            newConfig.factory(deepHandshakeFuzzer);
            return new FuzzerConfig[] { newConfig };
        }

        List<FuzzerConfig> generatedConfigs = new ArrayList<>();
        for (Ratio ratio : byte_flip_ratios) {
            FuzzerConfig newConfig = config.copy();
            DeepHandshakeFuzzer deepHandshakeFuzzer = builder.build();
            deepHandshakeFuzzer.fuzzer(newByteFlipFuzzer()
                    .minRatio(ratio.min())
                    .maxRatio(ratio.max()));
            newConfig.factory(deepHandshakeFuzzer);

            generatedConfigs.add(newConfig);
        }

        return generatedConfigs.toArray(new FuzzerConfig[0]);
    }

    private static FuzzerConfig[] enumerateBitFlipRatios(
            FuzzyStructFactoryBuilder builder, FuzzerConfig config) {

        // don't enumerate if a state is set
        if (config.hasState()) {
            FuzzerConfig newConfig = config.copy();
            DeepHandshakeFuzzer deepHandshakeFuzzer = builder.build();
            deepHandshakeFuzzer.fuzzer(newBitFlipFuzzer());
            newConfig.factory(deepHandshakeFuzzer);
            return new FuzzerConfig[] { newConfig };
        }

        List<FuzzerConfig> generatedConfigs = new ArrayList<>();
        for (Ratio ratio : bit_flip_ratios) {
            FuzzerConfig newConfig = config.copy();
            DeepHandshakeFuzzer deepHandshakeFuzzer = builder.build();
            deepHandshakeFuzzer.fuzzer(newBitFlipFuzzer()
                    .minRatio(ratio.min())
                    .maxRatio(ratio.max()));
            newConfig.factory(deepHandshakeFuzzer);

            generatedConfigs.add(newConfig);
        }

        return generatedConfigs.toArray(new FuzzerConfig[0]);
    }

    private static FuzzerConfig[] concatenate(FuzzerConfig[]... lists) {
        List<FuzzerConfig> result = new ArrayList<>();
        for (FuzzerConfig[] configs : lists) {
            result.addAll(List.of(configs));
        }
        return result.toArray(new FuzzerConfig[0]);
    }

    private interface FuzzyStructFactoryBuilder {
        DeepHandshakeFuzzer build();
    }
}
