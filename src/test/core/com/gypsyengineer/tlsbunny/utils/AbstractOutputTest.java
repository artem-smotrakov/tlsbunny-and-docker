package com.gypsyengineer.tlsbunny.utils;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AbstractOutputTest {

    @Test
    public void main() {
        CounterOutput checker = new CounterOutput();
        try (WrapperOutput wrapper = new WrapperOutput(checker)) {
            wrapper.add(null);
            wrapper.increaseIndent();
            wrapper.decreaseIndent();
            wrapper.prefix("test");
            wrapper.info("%s%n", "foo");
            wrapper.info("error", new RuntimeException("oops"));
            wrapper.achtung("%s%n", "foo");
            wrapper.achtung("error", new RuntimeException("oops"));
            wrapper.lines();
            wrapper.contains("oops");
            wrapper.flush();
        }

        assertEquals(12, checker.counter);
    }

    private static class WrapperOutput extends AbstractOutput {

        public WrapperOutput(Output output) {
            super(output);
        }
    }

    private static class CounterOutput implements Output {

        int counter = 0;

        @Override
        public Output add(OutputListener listener) {
            counter++;
            return this;
        }

        @Override
        public void increaseIndent() {
            counter++;
        }

        @Override
        public void decreaseIndent() {
            counter++;
        }

        @Override
        public void prefix(String prefix) {
            counter++;
        }

        @Override
        public void info(String format, Object... values) {
            counter++;
        }

        @Override
        public void info(String message, Throwable e) {
            counter++;
        }

        @Override
        public void achtung(String format, Object... values) {
            counter++;
        }

        @Override
        public void achtung(String message, Throwable e) {
            counter++;
        }

        @Override
        public List<String> lines() {
            counter++;
            return Collections.emptyList();
        }

        @Override
        public boolean contains(String line) {
            counter++;
            return false;
        }

        @Override
        public void flush() {
            counter++;
        }

        @Override
        public void close() {
            counter++;
        }
    }
}
