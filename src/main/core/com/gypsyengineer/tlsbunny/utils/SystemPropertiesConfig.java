package com.gypsyengineer.tlsbunny.utils;

import java.util.Objects;

public class SystemPropertiesConfig implements Config {

    public static final int DEFAULT_PARTS = 1;
    public static final int DEFAULT_TOTAL = 1000;
    public static final double DEFAULT_MIN_RATIO = 0.01;
    public static final double DEFAULT_MAX_RATIO = 0.05;
    public static final int DEFAULT_PORT = 10101;
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_THREADS = 1;
    public static final String default_server_certificate = "certs/server_cert.der";
    public static final String default_server_key = "certs/server_key.pkcs8";
    public static final String default_client_certificate = "certs/client_cert.der";
    public static final String default_client_key = "certs/client_key.pkcs8";
    public static final long DEFAULT_READ_TIMEOUT = 5000; // in millis
    public static final String EMPTY_STRING = "";

    private String host;
    private int port;
    private double minRatio;
    private double maxRatio;
    private int threads;
    private int parts;
    private long total;
    private String clientCertificate;
    private String clientKey;
    private String serverCertificate;
    private String serverKey;
    private long readTimeout;

    private SystemPropertiesConfig() {

    }

    @Override
    synchronized public SystemPropertiesConfig copy() {
        SystemPropertiesConfig clone = new SystemPropertiesConfig();
        clone.host = host;
        clone.port = port;
        clone.minRatio = minRatio;
        clone.maxRatio = maxRatio;
        clone.threads = threads;
        clone.parts = parts;
        clone.clientCertificate = clientCertificate;
        clone.clientKey = clientKey;
        clone.serverCertificate = serverCertificate;
        clone.serverKey = serverKey;
        clone.readTimeout = readTimeout;
        clone.total = total;

        return clone;
    }

    @Override
    synchronized public Config host(String host) {
        this.host = host;
        return this;
    }

    @Override
    synchronized public Config port(int port) {
        this.port = port;
        return this;
    }

    @Override
    synchronized public Config minRatio(double minRatio) {
        this.minRatio = minRatio;
        return this;
    }

    @Override
    synchronized public Config maxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
        return this;
    }

    @Override
    public Config total(long n) {
        total = n;
        return this;
    }

    @Override
    synchronized public Config parts(int parts) {
        this.parts = parts;
        return this;
    }

    @Override
    synchronized public Config readTimeout(long timeout) {
        readTimeout = timeout;
        return this;
    }

    @Override
    synchronized public Config clientCertificate(String path) {
        clientCertificate = path;
        return this;
    }

    @Override
    synchronized public Config clientKey(String path) {
        clientKey = path;
        return this;
    }

    @Override
    synchronized public Config serverCertificate(String path) {
        serverCertificate = path;
        return this;
    }

    @Override
    synchronized public Config serverKey(String path) {
        serverKey = path;
        return this;
    }

    @Override
    synchronized public String host() {
        return host;
    }

    @Override
    synchronized public int port() {
        return port;
    }

    @Override
    synchronized public double minRatio() {
        return minRatio;
    }

    @Override
    synchronized public double maxRatio() {
        return maxRatio;
    }

    @Override
    synchronized public int threads() {
        return threads;
    }

    @Override
    synchronized public int parts() {
        return parts;
    }

    @Override
    public long total() {
        return total;
    }

    @Override
    synchronized public String clientCertificate() {
        return clientCertificate;
    }

    @Override
    synchronized public String clientKey() {
        return clientKey;
    }

    @Override
    synchronized public String serverCertificate() {
        return serverCertificate;
    }

    @Override
    public String serverKey() {
        return serverKey;
    }

    @Override
    synchronized public long readTimeout() {
        return readTimeout;
    }

    @Override
    synchronized public String targetFilter() {
        return System.getProperty("tlsbunny.target.filter", EMPTY_STRING).trim();
    }

    @Override
    synchronized public boolean equals(Object o) {
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
                total == that.total &&
                readTimeout == that.readTimeout &&
                Objects.equals(host, that.host) &&
                Objects.equals(clientCertificate, that.clientCertificate) &&
                Objects.equals(clientKey, that.clientKey) &&
                Objects.equals(serverCertificate, that.serverCertificate) &&
                Objects.equals(serverKey, that.serverKey);
    }

    @Override
    synchronized public int hashCode() {
        return Objects.hash(host, port, minRatio, maxRatio, threads, parts,
                total, clientCertificate, clientKey,
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
        config.total = Long.getLong("tlsbunny.total", DEFAULT_TOTAL);
        config.serverCertificate = System.getProperty(
                "tlsbunny.server.cert", default_server_certificate);
        config.serverKey = System.getProperty(
                "tlsbunny.server.key", default_server_key);
        config.clientCertificate = System.getProperty(
                "tlsbunny.client.cert", default_client_certificate);
        config.clientKey = System.getProperty(
                "tlsbunny.client.key", default_client_key);
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
