package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.fuzzer.Fuzzer;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;

public abstract class FuzzyStructFactory<T> extends StructFactoryWrapper
        implements StructFactory, Fuzzer<T> {

    protected Target target;
    protected Output output;
    protected Fuzzer<T> fuzzer;

    public FuzzyStructFactory(StructFactory factory, Output output) {
        super(factory);
        this.output = output;
    }

    synchronized public FuzzyStructFactory target(Target target) {
        this.target = target;
        return this;
    }

    synchronized public FuzzyStructFactory target(String target) {
        return target(Target.valueOf(target));
    }

    synchronized public Target target() {
        return target;
    }

    synchronized public FuzzyStructFactory<T> fuzzer(Fuzzer<T> fuzzer) {
        this.fuzzer = fuzzer;
        return this;
    }

    synchronized public Fuzzer<T> fuzzer() {
        return fuzzer;
    }

    // implement methods from Fuzzer

    @Override
    synchronized public FuzzyStructFactory set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    synchronized public Output output() {
        return output;
    }

    @Override
    synchronized public long currentTest() {
        return fuzzer.currentTest();
    }

    @Override
    synchronized public void currentTest(long test) {
        fuzzer.currentTest(test);
    }

    @Override
    synchronized public boolean canFuzz() {
        return fuzzer.canFuzz();
    }

    @Override
    synchronized public void moveOn() {
        fuzzer.moveOn();
    }

}
