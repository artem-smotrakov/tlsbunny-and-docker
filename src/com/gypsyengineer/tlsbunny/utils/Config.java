package com.gypsyengineer.tlsbunny.utils;

import static com.gypsyengineer.tlsbunny.utils.Utils.info;
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
    public static final String DEFAULT_FUZZER = "MutatedClient";
    public static final String DEFAULT_TARGET = "tlsplaintext";
    public static final String DEFAULT_MODE = "byte_flip";

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
        YamlConfig yaml = load(getConfig());

        host = Objects.requireNonNullElse(
                System.getProperty("tlsbunny.host"), yaml.host);
        port = Objects.requireNonNullElse(
                Integer.getInteger("tlsbunny.port"), yaml.port);
        threads = Objects.requireNonNullElse(
                Integer.getInteger("tlsbunny.threads"), yaml.threads);
        minRatio = Objects.requireNonNullElse(
                getDouble("tlsbunny.min.ratio"), yaml.min_ratio);
        maxRatio = Objects.requireNonNullElse(
                getDouble("tlsbunny.max.ratio"), yaml.max_ratio);
        startTest = Objects.requireNonNullElse(
                Integer.getInteger("tlsbunny.start.test"), yaml.start_test);
        total = Objects.requireNonNullElse(
                Integer.getInteger("tlsbunny.total"), yaml.total);
        parts = Objects.requireNonNullElse(
                Integer.getInteger("tlsbunny.parts"), yaml.parts);

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
            fuzzerConfigs = List.of(
                    new FuzzerConfig(
                            DEFAULT_FUZZER, DEFAULT_TARGET, DEFAULT_MODE,
                            DEFAULT_MIN_RATIO, DEFAULT_MAX_RATIO));
        }
    }

    private static YamlConfig load(String filename) {
        YamlConfig yaml = new YamlConfig();
        Path path = Paths.get(filename);
        if (Files.isRegularFile(path)) {
            info("load config: %s", path);
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                yaml = new Yaml(new Constructor(YamlConfig.class)).load(reader);
            } catch (IOException e) {
                Utils.achtung("Could not load config file %s: %s", filename, e);
            }
        }

        return yaml;
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
        public String host = DEFAULT_HOST;
        public int port = DEFAULT_PORT;
        public int threads = Runtime.getRuntime().availableProcessors();
        public double min_ratio = DEFAULT_MIN_RATIO;
        public double max_ratio = DEFAULT_MAX_RATIO;
        public int start_test = DEFAULT_START_TEST;
        public int total = DEFAULT_TOTAL;
        public int parts = DEFAULT_PARTS;
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

    private static Double getDouble(String name) {
        String s = System.getProperty(name);
        if (s == null) {
            return null;
        }

        return Double.parseDouble(s);
    }

}
