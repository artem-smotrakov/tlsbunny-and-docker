package com.gypsyengineer.tlsbunny.fuzzer;

import com.gypsyengineer.tlsbunny.utils.Output;
import org.junit.Test;

import java.util.Arrays;

import static com.gypsyengineer.tlsbunny.fuzzer.BitFlipFuzzer.newBitFlipFuzzer;
import static com.gypsyengineer.tlsbunny.fuzzer.ByteFlipFuzzer.newByteFlipFuzzer;
import static org.junit.Assert.*;

public class FlipFuzzerTest {

    @Test
    public void iterateBitFlipFuzzer() {
        iterate(newBitFlipFuzzer());
    }

    @Test
    public void consistencyOfBitFlipFuzzer() {
        consistencyOf(newBitFlipFuzzer(), newBitFlipFuzzer());
    }

    @Test
    public void setTestInBitFlipFuzzer() {
        setTestIn(newBitFlipFuzzer());
    }

    @Test
    public void iterateByteFlipFuzzer() {
        iterate(newByteFlipFuzzer());
    }

    @Test
    public void consistencyOfByteFlipFuzzer() {
        consistencyOf(newByteFlipFuzzer(), newByteFlipFuzzer());
    }

    @Test
    public void setTestInByteFlipFuzzer() {
        setTestIn(newByteFlipFuzzer());
    }

    private static void iterate(Fuzzer<byte[]> fuzzer) {
        try (Output output = new Output()) {
            fuzzer.set(output);
            assertEquals(output, fuzzer.output());

            assertTrue(fuzzer.canFuzz());

            int expectedState = 0;

            int n = 200;
            byte[] array = new byte[n];

            int m = 300;
            for (int i = 0; i < m; i++) {
                assertTrue(fuzzer.canFuzz());
                assertEquals(expectedState, fuzzer.currentTest());

                byte[] fuzzed = fuzzer.fuzz(array);
                assertFalse(Arrays.equals(array, fuzzed));
                assertArrayEquals(fuzzed, fuzzer.fuzz(array));

                fuzzer.moveOn();
                expectedState++;
            }
        }
    }

    private static void consistencyOf(Fuzzer<byte[]> fuzzerOne, Fuzzer<byte[]> fuzzerTwo) {
        try (Output output = new Output()) {
            fuzzerOne.set(output);
            fuzzerTwo.set(output);

            assertTrue(fuzzerOne.canFuzz());
            assertTrue(fuzzerTwo.canFuzz());

            int expectedState = 0;

            int n = 1000;
            byte[] array = new byte[n];

            int m = 300;
            for (int i = 0; i < m; i++) {
                assertTrue(fuzzerOne.canFuzz());
                assertTrue(fuzzerTwo.canFuzz());

                assertEquals(expectedState, fuzzerOne.currentTest());
                assertEquals(expectedState, fuzzerTwo.currentTest());

                byte[] fuzzedOne = fuzzerOne.fuzz(array);
                byte[] fuzzedTwo = fuzzerTwo.fuzz(array);
                assertArrayEquals(fuzzedOne, fuzzedTwo);

                fuzzerOne.moveOn();
                fuzzerTwo.moveOn();

                expectedState++;
            }
        }
    }


    private static void setTestIn(Fuzzer<byte[]> fuzzer) {
        try (Output output = new Output()) {
            fuzzer.set(output);
            assertEquals(output, fuzzer.output());

            assertTrue(fuzzer.canFuzz());

            int n = 100;
            byte[] array = new byte[n];

            long expectedState = Long.MAX_VALUE - 50;
            fuzzer.currentTest(expectedState);
            while (expectedState < Long.MAX_VALUE) {
                assertTrue(fuzzer.canFuzz());
                assertEquals(expectedState, fuzzer.currentTest());

                byte[] fuzzed = fuzzer.fuzz(array);
                assertFalse(Arrays.equals(array, fuzzed));
                assertArrayEquals(fuzzed, fuzzer.fuzz(array));

                fuzzer.moveOn();
                expectedState++;
            }

            assertEquals(Long.MAX_VALUE, fuzzer.currentTest());
            assertFalse(fuzzer.canFuzz());
        }
    }

}
