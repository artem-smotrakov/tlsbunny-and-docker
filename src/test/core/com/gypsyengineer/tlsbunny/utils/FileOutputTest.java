package com.gypsyengineer.tlsbunny.utils;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertArrayEquals;

public class FileOutputTest {

    @Test
    public void main() throws IOException {
        Path path = Files.createTempFile(
                "tlsbunny", "file_output_test");
        try (Output output = new FileOutput(new LocalOutput(), path.toString())) {
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

            String[] lines = Files.readAllLines(path).toArray(new String[0]);
            assertArrayEquals(expected, lines);
        } finally {
            Files.delete(path);
        }

    }
}
