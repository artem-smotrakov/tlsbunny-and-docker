package com.gypsyengineer.tlsbunny.utils;

public class Parameters {
    
    public static final int DEFAULT_TEST_NUMBER = 10000;
    public static final double DEFAULT_MIN_RATIO = 0.01;
    public static final double DEFAULT_MAX_RATIO = 0.05;

    public static String getHost() {
        return System.getProperty("tlsbunny.host", "localhost").trim();
    }
    
    public static int getPort() {
        return Integer.getInteger("tlsbunny.port", 10101);
    }
    
    public static String getState() {
        String value = System.getProperty("tlsbunny.state", "");
        return value.trim();
    }
    
    public static String[] getTargets() {
        String value = System.getProperty("tlsbunny.target", "").trim();
        if (!value.isEmpty()) {
            return new String[] { value };
        }
        
        value = System.getProperty("tlsbunny.targets", "").trim();
        if (value.isEmpty()) {
            return new String[0];
        }
        
        String[] targets = value.split(",");
        for (int i=0; i<targets.length; i++) {
            targets[i] = targets[i].trim();
        }
        
        return targets;
    }
    
    public static int getTestsNumber() {
        return Integer.getInteger("tlsbunny.tests.number", DEFAULT_TEST_NUMBER);
    }
    
    public static double getMinRatio() {
        return getDouble("tlsbunny.min.ratio", DEFAULT_MIN_RATIO);
    }
    
    public static double getMaxRatio() {
        return getDouble("tlsbunny.max.ratio", DEFAULT_MAX_RATIO);
    }
    
    private static double getDouble(String name, double defaultValue) {
        String s = System.getProperty(name);
        if (s == null) {
            return defaultValue;
        }
        
        return Double.parseDouble(s);
    }
    
}
