package com.gypsyengineer.tlsbunny.tls13.test.client;

public class Config {

    public static final int DEFAULT_TOTAL = 1000;
    public static final int DEFAULT_PARTS = 4;
    public static final int DEFAULT_START_TEST = 0;
    public static final double DEFAULT_MIN_RATIO = 0.01;
    public static final double DEFAULT_MAX_RATIO = 0.05;
    public static final int DEFAULT_PORT = 10101;
    public static final String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_TARGET = "tls_plaintext";
    public static final String DEFAULT_MODE = "byte_flip";
    public static final int DEFAULT_THREADS = 3;

    private String host = System.getProperty("tlsbunny.host", DEFAULT_HOST).trim();
    private int port = Integer.getInteger("tlsbunny.port", DEFAULT_PORT);
    private String target = System.getProperty("tlsbunny.target", DEFAULT_TARGET).trim();
    private String mode = System.getProperty("tlsbunny.mode", DEFAULT_MODE).trim();
    private int total = Integer.getInteger("tlsbunny.total", DEFAULT_TOTAL);
    private double minRatio = getDouble("tlsbunny.min.ratio", DEFAULT_MIN_RATIO);
    private double maxRatio = getDouble("tlsbunny.max.ratio", DEFAULT_MAX_RATIO);
    private int threads = Integer.getInteger("tlsbunny.threads", DEFAULT_THREADS);
    private int parts = Integer.getInteger("tlsbunny.parts", DEFAULT_PARTS);
    private long startTest = Long.getLong("tlsbunny.start.test", DEFAULT_START_TEST);

    public Config host(String host) {
        this.host = host;
        return this;
    }

    public Config target(String target) {
        this.target = target;
        return this;
    }

    public Config mode(String mode) {
        this.mode = mode;
        return this;
    }

    public Config minRatio(double minRatio) {
        this.minRatio = minRatio;
        return this;
    }

    public Config maxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
        return this;
    }

    public Config total(int total) {
        this.total = total;
        return this;
    }

    public Config parts(int parts) {
        this.parts = parts;
        return this;
    }

    public Config startTest(long startTest) {
        this.startTest = startTest;
        return this;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String target() {
        return target;
    }

    public String mode() {
        return mode;
    }

    public int total() {
        return total;
    }

    public double minRatio() {
        return minRatio;
    }

    public double maxRatio() {
        return maxRatio;
    }

    public int threads() {
        return threads;
    }

    public int parts() {
        return parts;
    }

    public long startTest() {
        return startTest;
    }

    private static Double getDouble(String name, double defaultValue) {
        String s = System.getProperty(name);
        if (s == null) {
            return defaultValue;
        }

        return Double.parseDouble(s);
    }

}
