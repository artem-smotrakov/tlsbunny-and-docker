package com.gypsyengineer.tlsbunny.utils;

/**
 * Indicates that an object can take an Output instance.
 */
public interface HasOutput {
    void set(Output output);
    Output output();
}
