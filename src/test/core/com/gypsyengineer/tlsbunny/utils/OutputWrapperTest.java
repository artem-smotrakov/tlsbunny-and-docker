package com.gypsyengineer.tlsbunny.utils;

import com.gypsyengineer.tlsbunny.output.OutputWrapper;
import com.gypsyengineer.tlsbunny.output.Line;
import com.gypsyengineer.tlsbunny.output.Output;
import com.gypsyengineer.tlsbunny.output.OutputListener;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OutputWrapperTest {

    private static final Line null_line = null;
    private static final OutputListener null_listener = null;

    @Test
    public void main() {
        CounterOutput checker = new CounterOutput();
        try (TestWrapperOutput wrapper = new TestWrapperOutput(checker)) {
            wrapper.add(null_line);
            wrapper.add(null_listener);
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

        assertEquals(13, checker.counter);
    }

    private static class TestWrapperOutput extends OutputWrapper {

        public TestWrapperOutput(Output output) {
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
        public void add(Line line) {
            counter++;
        }

        @Override
        public List<Line> lines() {
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
