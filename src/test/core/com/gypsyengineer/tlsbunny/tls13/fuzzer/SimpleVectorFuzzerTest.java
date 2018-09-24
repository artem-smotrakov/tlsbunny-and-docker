package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.fuzzer.FuzzedVector;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.WhatTheHell;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.SimpleVectorFuzzer.newSimpleVectorFuzzer;
import static org.junit.Assert.*;

public class SimpleVectorFuzzerTest {

    @Test
    public void iterate() {
        try (Output output = new Output()) {
            SimpleVectorFuzzer fuzzer = new SimpleVectorFuzzer();

            fuzzer.set(output);
            assertEquals(output, fuzzer.output());

            assertTrue(fuzzer.canFuzz());

            int expectedState = 0;
            Vector<Byte> vector = Vector.wrap(1, new byte[] { 0, 1, 2});
            Vector<Byte> previous = null;
            while (fuzzer.canFuzz()) {
                assertEquals(expectedState, fuzzer.currentTest());

                Vector<Byte> fuzzed = fuzzer.fuzz(vector);
                assertNotEquals(vector, fuzzed);
                assertNotEquals(previous, fuzzed);
                assertEquals(fuzzed, fuzzer.fuzz(vector));
                assertTrue(fuzzed instanceof FuzzedVector);

                fuzzer.moveOn();
                expectedState++;
            }

            assertFalse(fuzzer.canFuzz());

            try {
                fuzzer.fuzz(vector);
                fail("expected an exception");
            } catch (WhatTheHell e) {
                // good
            }
        }
    }

    @Test
    public void setTest() {
        try (Output output = new Output()) {
            SimpleVectorFuzzer fuzzer = newSimpleVectorFuzzer();
            fuzzer.set(output);

            try {
                fuzzer.currentTest(Integer.MAX_VALUE / 2);
                fail("expected an exception");
            } catch (WhatTheHell e) {
                // good
            }

            int expectedState = 10;

            fuzzer.currentTest(expectedState);
            assertEquals(expectedState, fuzzer.currentTest());

            Vector<Byte> vector = Vector.wrap(1, new byte[] {});
            Vector<Byte> previous = null;
            while (fuzzer.canFuzz()) {
                assertEquals(expectedState, fuzzer.currentTest());

                Vector<Byte> fuzzed = fuzzer.fuzz(vector);
                assertNotEquals(vector, fuzzed);
                assertNotEquals(previous, fuzzed);
                assertEquals(fuzzed, fuzzer.fuzz(vector));
                assertTrue(fuzzed instanceof FuzzedVector);

                fuzzer.moveOn();
                expectedState++;
            }
        }
    }
}
