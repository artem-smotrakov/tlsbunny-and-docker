package com.gypsyengineer.tlsbunny.tls13.test;

import com.gypsyengineer.tlsbunny.tls13.fuzzer.Target;

public class SystemPropertiesConfig implements Config {

    public static final int DEFAULT_PARTS = 1;
    public static final int DEFAULT_START_TEST = 0;
    public static final int DEFAULT_END_TEST = 1000;
    public static final double DEFAULT_MIN_RATIO = 0.01;
    public static final double DEFAULT_MAX_RATIO = 0.05;
    public static final int DEFAULT_PORT = 10101;
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_THREADS = 3;
    public static final String DEFAULT_CLIENT_CERTIFICATE = "certs/client_cert.der";
    public static final String DEFAULT_CLIENT_KEY = "certs/client_key.pkcs8";
    public static final long DEFAULT_READ_TIMEOUT = 5000; // in millis
    public static final String EMPTY_STRING = "";

    String host;
    int port;
    double minRatio;
    double maxRatio;
    int threads;
    int parts;
    long startTest;
    long endTest;
    Target target;
    String clientCertificate;
    String clientKey;
    long readTimeout;

    private SystemPropertiesConfig() {

    }

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
    public Config readTimeout(long timeout) {
        readTimeout = timeout;
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
        return clientCertificate;
    }

    @Override
    public String clientKey() {
        return clientKey;
    }

    @Override
    public long readTimeout() {
        return readTimeout;
    }

    public String targetFilter() {
        return System.getProperty("tlsbunny.target.filter", EMPTY_STRING).trim();
    }

    public static SystemPropertiesConfig load() {
        SystemPropertiesConfig config = new SystemPropertiesConfig();

        config.host = System.getProperty("tlsbunny.host", DEFAULT_HOST).trim();
        config.port = Integer.getInteger("tlsbunny.port", DEFAULT_PORT);
        config.minRatio = getDouble("tlsbunny.min.ratio", DEFAULT_MIN_RATIO);
        config.maxRatio = getDouble("tlsbunny.max.ratio", DEFAULT_MAX_RATIO);
        config.threads = Integer.getInteger("tlsbunny.threads", DEFAULT_THREADS);
        config.parts = Integer.getInteger("tlsbunny.parts", DEFAULT_PARTS);
        config.startTest = Long.getLong("tlsbunny.start.test", DEFAULT_START_TEST);
        config.endTest = Long.getLong("tlsbunny.ebd.test", DEFAULT_END_TEST);
        config.clientCertificate = System.getProperty(
                "tlsbunny.client.cert", DEFAULT_CLIENT_CERTIFICATE);
        config.clientKey = System.getProperty(
                "tlsbunny.client.key", DEFAULT_CLIENT_KEY);
        config.readTimeout = Long.getLong("tlsbunny.read.timeout", DEFAULT_READ_TIMEOUT);

        return config;
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
