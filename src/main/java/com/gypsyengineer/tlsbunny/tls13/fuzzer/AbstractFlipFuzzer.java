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

        check(minRatio, maxRatio);
        this.minRatio = minRatio;
        this.maxRatio = maxRatio;

        if (endIndex == 0) {
            throw new IllegalArgumentException("what the hell? end index is zero!");
        }

        if (startIndex == endIndex && startIndex > 0) {
            throw new IllegalArgumentException(
                    "what the hell? end and start indexes are the same!");
        }

        if (endIndex >= 0 && startIndex > endIndex) {
            throw new IllegalArgumentException(
                    "what the hell? start index is greater than end index!");
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
        if (endIndex > 0 && endIndex < array.length) {
            return endIndex;
        }

        return array.length - 1;
    }

    public AbstractFlipFuzzer minRatio(double ratio) {
        minRatio = check(ratio);
        return this;
    }

    public AbstractFlipFuzzer maxRatio(double ratio) {
        maxRatio = check(ratio);
        return this;
    }

    public AbstractFlipFuzzer startIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "what the hell? start index is negative!");
        }

        if (endIndex >= 0 && index >= endIndex) {
            throw new IllegalArgumentException(
                    "what the hell? start index is greater than end index!");
        }

        startIndex = index;
        return this;
    }

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

    private static double check(double ratio) {
        if (ratio <= 0 || ratio > 1) {
            throw new IllegalArgumentException(
                    String.format("what the hell? wrong ratio: %.2f", ratio));
        }

        return ratio;
    }

    private static void check(double minRatio, double maxRatio) {
        check(minRatio);
        check(maxRatio);
        if (minRatio > maxRatio) {
            throw new IllegalArgumentException(
                    "what the hell? min ration should not be greater than max ratio!");
        }
    }

}
