package com.gypsyengineer.tlsbunny.utils;

/**
 * Indicates that an object can take an Output instance.
 */
public interface HasOutput<T> {
    T set(SimpleOutput output);
    SimpleOutput output();
}
