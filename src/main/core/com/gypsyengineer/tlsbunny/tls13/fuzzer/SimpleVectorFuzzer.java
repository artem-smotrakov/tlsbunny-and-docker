package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.fuzzer.FuzzedVector;
import com.gypsyengineer.tlsbunny.fuzzer.Fuzzer;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.WhatTheHell;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class SimpleVectorFuzzer implements Fuzzer<Vector<Byte>> {

    public static SimpleVectorFuzzer newSimpleVectorFuzzer() {
        return new SimpleVectorFuzzer();
    }

    private final Generator[] generators;
    private int state = 0;
    private Output output;

    public SimpleVectorFuzzer() {
        generators = new Generator[] {
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, vector.bytes()),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 1, vector.bytes()),
                (vector, output) -> new FuzzedVector(vector.lengthBytes(),
                        255, vector.bytes()),

                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, generateArray(1, 0x00)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, generateArray(100, 0x00)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, generateArray(255, 0x00)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 100, generateArray(1, 0x00)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 100, generateArray(100, 0x00)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 100, generateArray(255, 0x00)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 255, generateArray(1, 0x00)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 255, generateArray(100, 0x00)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 255, generateArray(255, 0x00)),

                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, generateArray(1, 0x17)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, generateArray(100, 0x17)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, generateArray(255, 0x17)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 100, generateArray(1, 0x17)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 100, generateArray(100, 0x17)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 100, generateArray(255, 0x17)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 255, generateArray(1, 0x17)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 255, generateArray(100, 0x17)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 255, generateArray(255, 0x17)),

                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, generateArray(1, 0xFF)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, generateArray(100, 0xFF)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, generateArray(255, 0xFF)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 100, generateArray(1, 0xFF)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 100, generateArray(100, 0xFF)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 100, generateArray(255, 0xFF)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 255, generateArray(1, 0xFF)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 255, generateArray(100, 0xFF)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 255, generateArray(255, 0xFF)),

                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, generateArray(1)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, generateArray(100)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 0, generateArray(255)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 100, generateArray(1)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 100, generateArray(100)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 100, generateArray(255)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 255, generateArray(1)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 255, generateArray(100)),
                (vector, output) -> new FuzzedVector(
                        vector.lengthBytes(), 255, generateArray(255)),
        };
    }

    @Override
    synchronized public void set(Output output) {
        this.output = output;
    }

    @Override
    synchronized public Output output() {
        return output;
    }

    @Override
    synchronized public long currentTest() {
        return state;
    }

    @Override
    synchronized public void currentTest(long test) {
        state = check(test);
    }

    @Override
    synchronized public boolean canFuzz() {
        return state <= generators.length - 1;
    }

    @Override
    synchronized public void moveOn() {
        if (state == Long.MAX_VALUE) {
            throw new IllegalStateException();
        }
        state++;
    }

    @Override
    synchronized public final Vector<Byte> fuzz(Vector<Byte> vector) {
        if (!canFuzz()) {
            throw new WhatTheHell("I can't fuzz anymore!");
        }

        try {
            return generators[state].run(vector, output);
        } catch (IOException e) {
            throw whatTheHell("unexpected exception", e);
        }
    }

    private int check(long state) {
        if (state < 0 || state > generators.length - 1) {
            throw whatTheHell(
                    "state should be in [0, %d], but %d received",
                    generators.length - 1, state);
        }

        return (int) state;
    }

    private static byte[] generateArray(int length) {
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = (byte) (0xFF & i);
        }
        return array;
    }

    private static byte[] generateArray(int length, int value) {
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = (byte) (0xFF & value);
        }
        return array;
    }

    private interface Generator {
        Vector<Byte> run (Vector<Byte> vector, Output output) throws IOException;
    }
}
