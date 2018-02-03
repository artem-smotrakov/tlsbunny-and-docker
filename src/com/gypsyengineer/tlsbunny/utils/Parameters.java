package com.gypsyengineer.tlsbunny.utils;

public class Parameters {

    public static final String EMPTY = "";
    public static final int DEFAULT_TEST_NUMBER = 10000;
    public static final double DEFAULT_MIN_RATIO = 0.01;
    public static final double DEFAULT_MAX_RATIO = 0.05;
    public static final int DEFAULT_PORT = 10101;
    public static final String DEFAULT_HOST = "localhost";

    // TODO: default number of threads should depend on a number of cores
    public static final int DEFAULT_THREADS = 3;

    public static String helpHost() {
        return String.format("-Dtlsbunny.host sets a hostname");
    }

    public static String getHost() {
        return System.getProperty("tlsbunny.host", DEFAULT_HOST).trim();
    }

    public static String helpPort() {
        return String.format("-Dtlsbunny.port sets a port number");
    }

    public static int getPort() {
        return Integer.getInteger("tlsbunny.port", DEFAULT_PORT);
    }

    public static String helpState() {
        return String.format("-Dtlsbunny.state sets fuzzer's state");
    }

    public static String getState() {
        String value = System.getProperty("tlsbunny.state", EMPTY);
        return value.trim();
    }

    public static String helpTargets() {
        return String.format("-Dtlsbunny.target and -Dtlsbunny.targets set what to fuzz");
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

    public static String helpMode() {
        return String.format("-Dtlsbunny.mode sets fuzzing mode");
    }

    public static String getMode() {
        return System.getProperty("tlsbunny.mode", EMPTY).trim();
    }

    public static String helpTestsNumber() {
        return String.format("-Dtlsbunny.tests.number sets a number of tests to run");
    }

    public static int getTestsNumber() {
        return Integer.getInteger("tlsbunny.tests.number", DEFAULT_TEST_NUMBER);
    }

    public static String helpRatios() {
        return String.format(
                "-Dtlsbunny.min.ratio and -Dtlsbunny.max.ratio set a ratio of data to be fuzzed");
    }

    public static double getMinRatio() {
        return getDouble("tlsbunny.min.ratio", DEFAULT_MIN_RATIO);
    }

    public static double getMaxRatio() {
        return getDouble("tlsbunny.max.ratio", DEFAULT_MAX_RATIO);
    }

    public static String helpThreads() {
        return String.format("-Dtlsbunny.threads sets a number of threads");
    }

    public static int getThreads() {
        return Integer.getInteger("tlsbunny.threads", DEFAULT_THREADS);
    }

    private static double getDouble(String name, double defaultValue) {
        String s = System.getProperty(name);
        if (s == null) {
            return defaultValue;
        }

        return Double.parseDouble(s);
    }

}
