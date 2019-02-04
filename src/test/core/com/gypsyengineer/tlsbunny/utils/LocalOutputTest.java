package com.gypsyengineer.tlsbunny.utils;

import com.gypsyengineer.tlsbunny.output.LocalOutput;
import com.gypsyengineer.tlsbunny.output.Output;
import com.gypsyengineer.tlsbunny.output.OutputListener;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class LocalOutputTest {

    @Test
    public void main() {
        try (Output output = new LocalOutput()) {
            output.prefix("test");
            output.achtung("foo");
            output.increaseIndent();
            output.info("bar");
            output.decreaseIndent();;
            output.info("test");
            output.flush();

            String[] expected = {
                    "[test] achtung: foo",
                    "[test]     bar",
                    "[test] test"
            };

            String[] lines = output.lines().toArray(new String[0]);
            assertArrayEquals(expected, lines);
            assertTrue(output.contains("foo"));
        }
    }

    @Test
    public void exception() {
        try (Output output = new LocalOutput()) {
            output.prefix("test");
            output.info("error", new IOException("oops"));
            output.flush();

            assertTrue(output.contains("[test] java.io.IOException: oops"));
        }
    }

    @Test
    public void listener() {
        try (Output output = new LocalOutput()) {

            output.add(new OutputListener() {

                @Override
                public void receivedInfo(String... strings) {
                    assertNotNull(strings);
                    assertEquals(1, strings.length);
                    assertEquals("error", strings[0]);
                }

                @Override
                public void receivedImportant(String... strings) {
                    fail("should not be here!");
                }

                @Override
                public void receivedAchtung(String... strings) {
                    fail("should not be here!");
                }
            });

            output.prefix("test");
            output.info("error");
            output.flush();

            assertTrue(output.contains("[test] error"));
        }
    }
}
