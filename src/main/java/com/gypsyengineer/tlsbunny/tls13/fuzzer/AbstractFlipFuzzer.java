package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.Random;

public abstract class AbstractFlipFuzzer implements Fuzzer<byte[]> {

    public static final double DEFAULT_MIN_RATIO = 0.01;
    public static final double DEFAULT_MAX_RATIO = 0.05;

    static final int FROM_THE_BEGINNING = 0;
    static final int NOT_SPECIFIED = -1;

    double minRatio;
    double maxRatio;
    private int startIndex;
    private int endIndex;
    final Random random;

    Output output;

    long state = 0;
    long end = Long.MAX_VALUE;

    public AbstractFlipFuzzer() {
        this(DEFAULT_MIN_RATIO, DEFAULT_MAX_RATIO, FROM_THE_BEGINNING, NOT_SPECIFIED);
    }

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
            // TODO: should it run if array.length < endIndex ?
            return endIndex;
        }

        return array.length;
    }

    // TODO: check
    public AbstractFlipFuzzer minRatio(double ratio) {
        minRatio = ratio;
        return this;
    }

    // TODO: check
    public AbstractFlipFuzzer maxRatio(double ratio) {
        maxRatio = ratio;
        return this;
    }

    // TODO: check
    public AbstractFlipFuzzer startIndex(int index) {
        startIndex = index;
        return this;
    }

    // TODO: check
    public AbstractFlipFuzzer endIndex(int index) {
        endIndex = index;
        return this;
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

    @Override
    public final byte[] fuzz(byte[] array) {
        random.setSeed(state);
        return fuzzImpl(array);
    }

    @Override
    public void setOutput(Output output) {
        this.output = output;
    }

    @Override
    public Output getOutput() {
        return output;
    }

    protected abstract byte[] fuzzImpl(byte[] array);

    double getRatio() {
        return minRatio + (maxRatio - minRatio) * random.nextDouble();
    }

}
