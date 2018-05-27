package com.gypsyengineer.tlsbunny.tls13.fuzzer;

public enum Mode {

    // mutation-based strategies
    byte_flip,
    bit_flip,

    // semi-generation-based strategies
    semi_mutated_vector
}