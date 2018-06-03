package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.utils.Output;

public interface Fuzzer<T> {

    String getState();
    void setState(String state);
    boolean canFuzz();
    T fuzz(T object);
    void moveOn();

    long getTest();
    void setStartTest(long test);
    void setEndTest(long test);

    void setOutput(Output output);
    Output getOutput();
}
