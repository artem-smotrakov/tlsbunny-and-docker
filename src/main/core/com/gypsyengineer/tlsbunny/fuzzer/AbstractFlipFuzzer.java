package com.gypsyengineer.tlsbunny.fuzzer;

import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.Random;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public abstract class AbstractFlipFuzzer implements Fuzzer<byte[]> {

    public static final double DEFAULT_MIN_RATIO = 0.01;
    public static final double DEFAULT_MAX_RATIO = 0.05;

    static final int FROM_THE_BEGINNING = 0;
    static final int NOT_SPECIFIED = -1;

    private int startIndex;
    private int endIndex;

    protected double minRatio;
    protected double maxRatio;
    protected final Random random;
    protected Output output;
    protected long state = 0;

    public AbstractFlipFuzzer() {
        this(DEFAULT_MIN_RATIO, DEFAULT_MAX_RATIO, FROM_THE_BEGINNING, NOT_SPECIFIED);
    }

    public AbstractFlipFuzzer(double minRatio, double maxRatio,
            int startIndex, int endIndex) {

        check(minRatio, maxRatio);
        this.minRatio = minRatio;
        this.maxRatio = maxRatio;

        if (endIndex == 0) {
            throw whatTheHell("end index is None!");
        }

        if (startIndex == endIndex && startIndex > 0) {
            throw whatTheHell("end and start indexes are the same!");
        }

        if (endIndex >= 0 && startIndex > endIndex) {
            throw whatTheHell("start index is greater than end index!");
        }
        this.startIndex = startIndex;
        this.endIndex = endIndex;

        random = new Random(state);
        random.setSeed(state);
    }

    synchronized public AbstractFlipFuzzer minRatio(double ratio) {
        minRatio = check(ratio);
        return this;
    }

    synchronized public AbstractFlipFuzzer maxRatio(double ratio) {
        maxRatio = check(ratio);
        return this;
    }

    synchronized public AbstractFlipFuzzer startIndex(int index) {
        if (index < 0) {
            throw whatTheHell("start index is negative!");
        }

        if (endIndex >= 0 && index >= endIndex) {
            throw whatTheHell("start index is greater than end index!");
        }

        startIndex = index;
        return this;
    }

    synchronized public AbstractFlipFuzzer endIndex(int index) {
        endIndex = index;
        return this;
    }

    @Override
    synchronized public long currentTest() {
        return state;
    }

    @Override
    synchronized public void currentTest(long test) {
        state = test;
    }

    @Override
    synchronized public boolean canFuzz() {
        return state < Long.MAX_VALUE;
    }

    @Override
    synchronized public void moveOn() {
        if (state == Long.MAX_VALUE) {
            throw whatTheHell("I can't move on because max state is reached!");
        }
        state++;
        random.setSeed(state);
    }

    @Override
    synchronized public final byte[] fuzz(byte[] array) {
        random.setSeed(state);
        return fuzzImpl(array);
    }

    @Override
    synchronized public void set(Output output) {
        this.output = output;
    }

    @Override
    synchronized public Output output() {
        return output;
    }

    abstract byte[] fuzzImpl(byte[] array);

    protected int getStartIndex() {
        if (startIndex > 0) {
            return startIndex;
        }

        return 0;
    }

    protected int getEndIndex(byte[] array) {
        if (endIndex > 0 && endIndex < array.length) {
            return endIndex;
        }

        return array.length - 1;
    }

    protected double getRatio() {
        return minRatio + (maxRatio - minRatio) * random.nextDouble();
    }

    private static double check(double ratio) {
        if (ratio <= 0 || ratio > 1) {
            throw whatTheHell("wrong ratio: %.2f", ratio);
        }

        return ratio;
    }

    private static void check(double minRatio, double maxRatio) {
        check(minRatio);
        check(maxRatio);
        if (minRatio > maxRatio) {
            throw whatTheHell("min ration should not be greater than max ratio!");
        }
    }

}