package com.gypsyengineer.tlsbunny.tls13.test.client;

public class Config {

    public static final int DEFAULT_TOTAL = 1000;
    public static final int DEFAULT_PARTS = 4;
    public static final int DEFAULT_START_TEST = 0;
    public static final double DEFAULT_MIN_RATIO = 0.01;
    public static final double DEFAULT_MAX_RATIO = 0.05;
    public static final int DEFAULT_PORT = 10101;
    public static final String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_TARGET = "tlsplaintext";
    public static final String DEFAULT_MODE = "byte_flip";

    public String getHost() {
        return System.getProperty("tlsbunny.host", DEFAULT_HOST).trim();
    }

    public int getPort() {
        return Integer.getInteger("tlsbunny.port", DEFAULT_PORT);
    }

    public String getTarget() {
        return System.getProperty("tlsbunny.target", DEFAULT_TARGET).trim();
    }

    public int getTotal() {
        return Integer.getInteger("tlsbunny.total", DEFAULT_TOTAL);
    }

    public double getMinRatio() {
        return getDouble("tlsbunny.min.ratio", DEFAULT_MIN_RATIO);
    }

    public double getMaxRatio() {
        return getDouble("tlsbunny.max.ratio", DEFAULT_MAX_RATIO);
    }

    private static Double getDouble(String name, double default_value) {
        String s = System.getProperty(name);
        if (s == null) {
            return default_value;
        }

        return Double.parseDouble(s);
    }

}
