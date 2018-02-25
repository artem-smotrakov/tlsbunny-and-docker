package com.gypsyengineer.tlsbunny.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

// TODO: system properties should have higher priority
public enum Config {

    // singleton
    Instance;

    public static final String EMPTY = "";

    // default settings
    public static final int DEFAULT_TOTAL = 1000;
    public static final int DEFAULT_PARTS = 4;
    public static final int DEFAULT_START_TEST = 0;
    public static final double DEFAULT_MIN_RATIO = 0.01;
    public static final double DEFAULT_MAX_RATIO = 0.05;
    public static final int DEFAULT_PORT = 10101;
    public static final String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_CONFIG = "config.yml";

    // TODO: default number of threads should depend on a number of CPU cores
    public static final int DEFAULT_THREADS = 3;

    // common parameters
    private final String host;
    private final int port;
    private final int threads;
    private final double minRatio;
    private final double maxRatio;
    private final int startTest;
    private final int total;
    private final int parts;
    private final List<FuzzerConfig> fuzzerConfigs;

    private Config() {
        YamlConfig yaml = new YamlConfig();
        Path filename = Paths.get(getConfig());
        try (BufferedReader reader = Files.newBufferedReader(filename)) {
            yaml = new Yaml(new Constructor(YamlConfig.class)).load(reader);
        } catch (IOException e) {
            Utils.achtung("Could not load config file %s: %s", filename, e);
        }

        host = Objects.requireNonNullElse(yaml.host,
                System.getProperty("tlsbunny.host", DEFAULT_HOST)).trim();
        port = Objects.requireNonNullElse(yaml.port,
                Integer.getInteger("tlsbunny.port", DEFAULT_PORT));
        threads = Objects.requireNonNullElse(yaml.threads,
                Integer.getInteger("tlsbunny.threads", DEFAULT_THREADS));
        minRatio = Objects.requireNonNullElse(yaml.min_ratio,
                getDouble("tlsbunny.min.ratio", DEFAULT_MIN_RATIO));
        maxRatio = Objects.requireNonNullElse(yaml.max_ratio,
                getDouble("tlsbunny.max.ratio", DEFAULT_MAX_RATIO));
        startTest = Objects.requireNonNullElse(yaml.start_test,
                Integer.getInteger("tlsbunny.start.test", DEFAULT_START_TEST));
        total = Objects.requireNonNullElse(yaml.total,
                Integer.getInteger("tlsbunny.total", DEFAULT_TOTAL));
        parts = Objects.requireNonNullElse(yaml.parts,
                Integer.getInteger("tlsbunny.parts", DEFAULT_PARTS));

        if (yaml.fuzzers != null && !yaml.fuzzers.isEmpty()) {
            List<FuzzerConfig> list = new ArrayList<>();
            for (YamlFuzzerConfig yamlFuzzerConfig: yaml.fuzzers) {
                list.add(new FuzzerConfig(
                        yamlFuzzerConfig.fuzzer,
                        yamlFuzzerConfig.target,
                        yamlFuzzerConfig.mode,
                        yamlFuzzerConfig.min_ratio,
                        yamlFuzzerConfig.max_ratio));
            }

            fuzzerConfigs = Collections.unmodifiableList(list);
        } else {
            // default fuzzer configuration
            // TODO: don't use hardcoded strings here
            fuzzerConfigs = List.of(
                    new FuzzerConfig("MutationClient", "tlsplaintext", "bit_flip",
                            DEFAULT_MIN_RATIO, DEFAULT_MAX_RATIO));
        }
    }

    // TODO: different fuzzers may require different fields,
    //       but the current disign doesn't seem to be flexible enough for that
    public static class FuzzerConfig {

        private final String fuzzer;
        private final String target;
        private final String mode;
        private final double minRatio;
        private final double maxRatio;

        public FuzzerConfig(String fuzzer, String target, String mode,
                double minRatio, double maxRatio) {
            this.fuzzer = fuzzer;
            this.target = target;
            this.mode = mode;
            this.minRatio = minRatio;
            this.maxRatio = maxRatio;
        }

        public String getFuzzer() {
            return fuzzer;
        }

        public String getTarget() {
            return target;
        }

        public String getMode() {
            return mode;
        }

        public double getMinRatio() {
            return minRatio;
        }

        public double getMaxRatio() {
            return maxRatio;
        }

    }

    private static class YamlConfig {
        public String host;
        public int port;
        public int threads;
        public double min_ratio;
        public double max_ratio;
        public int start_test;
        public int total;
        public int parts;
        public List<YamlFuzzerConfig> fuzzers;
    }

    private static class YamlFuzzerConfig {
        public String fuzzer;
        public String target;
        public String mode;
        public double min_ratio;
        public double max_ratio;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getStartTest() {
        return startTest;
    }

    public int getTotal() {
        return total;
    }

    public double getMinRatio() {
        return minRatio;
    }

    public double getMaxRatio() {
        return maxRatio;
    }

    public int getThreads() {
        return threads;
    }

    public int getParts() {
        return parts;
    }

    public List<FuzzerConfig> getFuzzerConfigs() {
        return fuzzerConfigs;
    }

    public static String getConfig() {
        return System.getProperty("tlsbunny.config", DEFAULT_CONFIG).trim();
    }

    public static String[] getTargets() {
        String value = System.getProperty("tlsbunny.target", EMPTY).trim();
        if (!value.isEmpty()) {
            return new String[] { value };
        }

        value = System.getProperty("tlsbunny.targets", EMPTY).trim();
        if (value.isEmpty()) {
            return new String[0];
        }

        String[] targets = value.split(",");
        for (int i=0; i<targets.length; i++) {
            targets[i] = targets[i].trim();
        }

        return targets;
    }

    public static String helpConfig() {
        return String.format("-Dtlsbunny.config sets a configuration file");
    }

    public static String helpHost() {
        return String.format("-Dtlsbunny.host sets a hostname");
    }

    public static String helpPort() {
        return String.format("-Dtlsbunny.port sets a port number");
    }

    public static String helpStartTest() {
        return String.format("-Dtlsbunny.start.test sets a first test");
    }

    public static String helpTargets() {
        return String.format("-Dtlsbunny.target and -Dtlsbunny.targets set what to fuzz");
    }

    public static String helpTotal() {
        return String.format("-Dtlsbunny.total sets a total test number for a single fuzzer");
    }

    public static String helpParts() {
        return String.format("-Dtlsbunny.parts sets a total test number for a single fuzzer");
    }

    public static String helpRatios() {
        return String.format(
                "-Dtlsbunny.min.ratio and -Dtlsbunny.max.ratio set a ratio of data to be fuzzed");
    }

    public static String helpThreads() {
        return String.format("-Dtlsbunny.threads sets a number of threads");
    }

    private static double getDouble(String name, double defaultValue) {
        String s = System.getProperty(name);
        if (s == null) {
            return defaultValue;
        }

        return Double.parseDouble(s);
    }

}
