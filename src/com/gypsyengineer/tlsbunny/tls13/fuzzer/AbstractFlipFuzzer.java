package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import java.util.Random;

public abstract class AbstractFlipFuzzer implements Fuzzer<byte[]> {

    final double minRatio;
    final double maxRatio;
    final int startIndex;
    final int endIndex;
    final Random random;

    long state = 0;

    public AbstractFlipFuzzer(double minRatio, double maxRatio,
            int startIndex, int endIndex) {

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
    public void moveOn() {
        if (state == Long.MAX_VALUE) {
            throw new IllegalStateException();
        }
        state++;
        random.setSeed(state);
    }

    double getRatio() {
        return minRatio + (maxRatio - minRatio) * random.nextDouble();
    }

}
