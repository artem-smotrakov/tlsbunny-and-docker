package com.gypsyengineer.tlsbunny.tls13.fuzzer;

public interface Fuzzer<T> {

    long NO_LIMIT = -1;

    String getState();
    void setState(String state);
    boolean canFuzz();
    T fuzz(T object);
    void moveOn();

    long getTest();
    void setStartTest(long test);
    void setEndTest(long test);
}
