package com.gypsyengineer.tlsbunny.utils;

public class Parameters {
    
    public static final int DEFAULT_TEST_NUMBER = 10000;
    public static final int DEFAULT_SEED = 1;

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
    
    public static int getTestNumber() {
        return Integer.getInteger("tlsbunny.tests.number", DEFAULT_TEST_NUMBER);
    }
    
    public static int getSeed() {
        return Integer.getInteger("tlsbunny.seed", DEFAULT_SEED);
    }
    
}
