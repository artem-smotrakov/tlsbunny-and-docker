package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.utils.Output;

public interface Fuzzer<T> {

    enum Type {
        mutated_struct_factory,
        semi_mutated_legacy_session_id_struct_factory
    }

    long NO_LIMIT = -1;

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
