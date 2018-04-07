package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import java.util.Random;

public abstract class AbstractFlipFuzzer implements Fuzzer<byte[]> {

    static final int FROM_THE_BEGINNING = 0;
    static final int NOT_SPECIFIED = -1;

    final double minRatio;
    final double maxRatio;
    private final int startIndex;
    private final int endIndex;
    final Random random;

    long state = 0;
    long end = Long.MAX_VALUE;

    public AbstractFlipFuzzer(double minRatio, double maxRatio,
            int startIndex, int endIndex) {

        if (minRatio <= 0 || maxRatio > 1 || minRatio > maxRatio) {
            throw new IllegalArgumentException();
        }
        this.minRatio = minRatio;
        this.maxRatio = maxRatio;

        if (endIndex == 0) {
            throw new IllegalArgumentException("end == 0");
        }

        if (startIndex == endIndex && startIndex > 0) {
            throw new IllegalArgumentException("start == end");
        }

        if (endIndex >= 0 && startIndex > endIndex) {
            throw new IllegalArgumentException("start > end");
        }
        this.startIndex = startIndex;
        this.endIndex = endIndex;

        random = new Random(state);
        random.setSeed(state);
    }

    int getStartIndex() {
        if (startIndex > 0) {
            return startIndex;
        }

        return 0;
    }

    int getEndIndex(byte[] array) {
        if (endIndex > 0) {
            // TODO: should it check if array.length < endIndex ?
            return endIndex;
        }

        return array.length;
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
        setStartTest(value);
    }

    @Override
    public void setStartTest(long state) {
        this.state = state;
    }

    @Override
    public void setEndTest(long end) {
        this.end = end;
    }

    @Override
    public long getTest() {
        return state;
    }

    @Override
    public boolean canFuzz() {
        return state <= end;
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
