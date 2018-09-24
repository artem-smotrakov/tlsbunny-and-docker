package com.gypsyengineer.tlsbunny.fuzzer;

import com.gypsyengineer.tlsbunny.utils.HasOutput;

public interface Fuzzer<T> extends HasOutput {
    boolean canFuzz();
    T fuzz(T object);
    void moveOn();
    long currentTest();
    void currentTest(long test);
}
