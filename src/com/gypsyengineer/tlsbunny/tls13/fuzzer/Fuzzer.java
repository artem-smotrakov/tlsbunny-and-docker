package com.gypsyengineer.tlsbunny.tls13.fuzzer;

public interface Fuzzer<T> {

    String getState();
    void setState(String state);
    boolean canFuzz();
    T fuzz(T object);
    void moveOn();
}
