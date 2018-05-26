package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Vector;

import java.io.IOException;
import java.util.List;

public class MutatedVector<T> implements Vector<T> {

    @Override
    public int size() {
        throw new UnsupportedOperationException("no mutated vectors for you!");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("no mutated vectors for you!");
    }

    @Override
    public T get(int index) {
        throw new UnsupportedOperationException("no mutated vectors for you!");
    }

    @Override
    public T first() {
        throw new UnsupportedOperationException("no mutated vectors for you!");
    }

    @Override
    public void add(T object) {
        throw new UnsupportedOperationException("no mutated vectors for you!");
    }

    @Override
    public void set(int index, T object) {
        throw new UnsupportedOperationException("no mutated vectors for you!");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("no mutated vectors for you!");
    }

    @Override
    public List<T> toList() {
        throw new UnsupportedOperationException("no mutated vectors for you!");
    }

    @Override
    public byte[] bytes() throws IOException {
        throw new UnsupportedOperationException("no mutated vectors for you!");
    }

    @Override
    public int encodingLength() {
        throw new UnsupportedOperationException("no mutated vectors for you!");
    }

    @Override
    public byte[] encoding() throws IOException {
        throw new UnsupportedOperationException("no mutated vectors for you!");
    }
}
