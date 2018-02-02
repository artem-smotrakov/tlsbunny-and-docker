package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import java.util.Random;

public class ByteFlipFuzzer implements Fuzzer<byte[]> {

    private long state = 0;
    
    private final double minRatio;
    private final double maxRatio;
    private final int startIndex;
    private final int endIndex;
    private final Random random;
    
    public ByteFlipFuzzer(
            double minRatio, double maxRatio, int startIndex, int endIndex) {
        
        if (minRatio <= 0 || maxRatio > 1 || minRatio > maxRatio) {
            throw new IllegalArgumentException();
        }
        this.minRatio = minRatio;
        this.maxRatio = maxRatio;
        
        if (startIndex < 0 || endIndex < 0 || startIndex > endIndex) {
            throw new IllegalArgumentException();
        }
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        
        random = new Random(state);
        random.setSeed(state);
    }
    
    @Override
    public String getState() {
        return Long.toString(state);
    }

    @Override
    public void setState(String state) {
        long value = Long.parseLong(state);
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        
        this.state = value;
    }

    @Override
    public boolean canFuzz() {
        return state < Long.MAX_VALUE;
    }

    @Override
    public byte[] fuzz(byte[] array) {
        byte[] fuzzed = array.clone();
        double ratio = minRatio + (maxRatio - minRatio) * random.nextDouble();
        int n = (int) Math.ceil((endIndex - startIndex) * ratio);
        for (int i = 0; i < n; i++) {
            int pos = startIndex + random.nextInt(endIndex - startIndex);
            fuzzed[pos] = (byte) random.nextInt(256);
        }
        
        return fuzzed;
    }

    @Override
    public void moveOn() {
        if (state == Long.MAX_VALUE) {
            throw new IllegalStateException();
        }
        
        state++;
        random.setSeed(state);
    }
    
}
