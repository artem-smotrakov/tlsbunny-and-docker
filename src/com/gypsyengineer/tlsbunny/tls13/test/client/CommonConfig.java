package com.gypsyengineer.tlsbunny.tls13.test.client;

public class CommonConfig implements Config {

    public static final int DEFAULT_PARTS = 4;
    public static final int DEFAULT_START_TEST = 0;
    public static final int DEFAULT_END_TEST = 1000;
    public static final double DEFAULT_MIN_RATIO = 0.01;
    public static final double DEFAULT_MAX_RATIO = 0.05;
    public static final int DEFAULT_PORT = 10101;
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_THREADS = 3;
    public static final String DEFAULT_CLIENT_CERTIFICATE = "certs/client_cert.der";
    public static final String DEFAULT_CLIENT_KEY = "certs/client_key.pkcs8";

    private String host = System.getProperty("tlsbunny.host", DEFAULT_HOST).trim();
    private int port = Integer.getInteger("tlsbunny.port", DEFAULT_PORT);
    private double minRatio = getDouble("tlsbunny.min.ratio", DEFAULT_MIN_RATIO);
    private double maxRatio = getDouble("tlsbunny.max.ratio", DEFAULT_MAX_RATIO);
    private int threads = Integer.getInteger("tlsbunny.threads", DEFAULT_THREADS);
    private int parts = Integer.getInteger("tlsbunny.parts", DEFAULT_PARTS);
    private long startTest = Long.getLong("tlsbunny.start.test", DEFAULT_START_TEST);
    private long endTest = Long.getLong("tlsbunny.ebd.test", DEFAULT_END_TEST);

    @Override
    public Config minRatio(double minRatio) {
        this.minRatio = minRatio;
        return this;
    }

    @Override
    public Config maxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
        return this;
    }

    @Override
    public Config parts(int parts) {
        this.parts = parts;
        return this;
    }

    @Override
    public Config startTest(long startTest) {
        this.startTest = startTest;
        return this;
    }

    @Override
    public Config endTest(long endTest) {
        this.endTest = endTest;
        return this;
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public double minRatio() {
        return minRatio;
    }

    @Override
    public double maxRatio() {
        return maxRatio;
    }

    @Override
    public int threads() {
        return threads;
    }

    @Override
    public int parts() {
        return parts;
    }

    @Override
    public long startTest() {
        return startTest;
    }

    @Override
    public long endTest() {
        return endTest;
    }

    @Override
    public String clientCertificate() {
        return System.getProperty("tlsbunny.client.cert", DEFAULT_CLIENT_CERTIFICATE);
    }

    @Override
    public String clientKey() {
        return System.getProperty("tlsbunny.client.key", DEFAULT_CLIENT_KEY);
    }

    @Override
    public CommonConfig copy() {
        CommonConfig clone = new CommonConfig();
        clone.host = host;
        clone.port = port;
        clone.minRatio = minRatio;
        clone.maxRatio = maxRatio;
        clone.threads = threads;
        clone.parts = parts;
        clone.startTest = startTest;
        clone.endTest = endTest;

        return clone;
    }

    private static Double getDouble(String name, double defaultValue) {
        String s = System.getProperty(name);
        if (s == null) {
            return defaultValue;
        }

        return Double.parseDouble(s);
    }

    public Runnable create() {
        throw new UnsupportedOperationException();
    }
}
