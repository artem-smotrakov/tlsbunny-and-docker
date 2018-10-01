package com.gypsyengineer.tlsbunny.utils;

import java.util.Objects;

public class SystemPropertiesConfig implements Config {

    public static final int DEFAULT_PARTS = 1;
    public static final int DEFAULT_START_TEST = 0;
    public static final int DEFAULT_END_TEST = 1000;
    public static final double DEFAULT_MIN_RATIO = 0.01;
    public static final double DEFAULT_MAX_RATIO = 0.05;
    public static final int DEFAULT_PORT = 10101;
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_THREADS = 1;
    public static final String DEFAULT_SERVER_CERTIFICATE = "certs/server_cert.der";
    public static final String DEFAULT_SERVER_KEY = "certs/server_key.pkcs8";
    public static final String DEFAULT_CLIENT_CERTIFICATE = "certs/client_cert.der";
    public static final String DEFAULT_CLIENT_KEY = "certs/client_key.pkcs8";
    public static final long DEFAULT_READ_TIMEOUT = 5000; // in millis
    public static final String EMPTY_STRING = "";

    private String host;
    private int port;
    private double minRatio;
    private double maxRatio;
    private int threads;
    private int parts;
    private long startTest;
    private long endTest;
    private String clientCertificate;
    private String clientKey;
    private String serverCertificate;
    private String serverKey;
    private long readTimeout;

    private SystemPropertiesConfig() {

    }

    @Override
    public SystemPropertiesConfig copy() {
        SystemPropertiesConfig clone = new SystemPropertiesConfig();
        clone.host = host;
        clone.port = port;
        clone.minRatio = minRatio;
        clone.maxRatio = maxRatio;
        clone.threads = threads;
        clone.parts = parts;
        clone.startTest = startTest;
        clone.endTest = endTest;
        clone.clientCertificate = clientCertificate;
        clone.clientKey = clientKey;
        clone.serverCertificate = serverCertificate;
        clone.serverKey = serverKey;
        clone.readTimeout = readTimeout;

        return clone;
    }

    @Override
    public Config host(String host) {
        this.host = host;
        return this;
    }

    @Override
    public Config port(int port) {
        this.port = port;
        return this;
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
    public Config clientCertificate(String path) {
        clientCertificate = path;
        return this;
    }

    @Override
    public Config clientKey(String path) {
        clientKey = path;
        return this;
    }

    @Override
    public Config serverCertificate(String path) {
        serverCertificate = path;
        return this;
    }

    @Override
    public Config serverKey(String path) {
        serverKey = path;
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
    public String serverCertificate() {
        return serverCertificate;
    }

    @Override
    public String serverKey() {
        return serverKey;
    }

    @Override
    public long readTimeout() {
        return readTimeout;
    }

    @Override
    public String targetFilter() {
        return System.getProperty("tlsbunny.target.filter", EMPTY_STRING).trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SystemPropertiesConfig that = (SystemPropertiesConfig) o;
        return port == that.port &&
                Double.compare(that.minRatio, minRatio) == 0 &&
                Double.compare(that.maxRatio, maxRatio) == 0 &&
                threads == that.threads &&
                parts == that.parts &&
                startTest == that.startTest &&
                endTest == that.endTest &&
                readTimeout == that.readTimeout &&
                Objects.equals(host, that.host) &&
                Objects.equals(clientCertificate, that.clientCertificate) &&
                Objects.equals(clientKey, that.clientKey) &&
                Objects.equals(serverCertificate, that.serverCertificate) &&
                Objects.equals(serverKey, that.serverKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, minRatio, maxRatio, threads, parts,
                startTest, endTest, clientCertificate, clientKey,
                serverCertificate, serverKey, readTimeout);
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
        config.endTest = Long.getLong("tlsbunny.end.test", DEFAULT_END_TEST);
        config.serverCertificate = System.getProperty(
                "tlsbunny.server.cert", DEFAULT_SERVER_CERTIFICATE);
        config.serverKey = System.getProperty(
                "tlsbunny.server.key", DEFAULT_SERVER_KEY);
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

}
