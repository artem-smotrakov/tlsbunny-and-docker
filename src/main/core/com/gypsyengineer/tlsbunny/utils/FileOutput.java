package com.gypsyengineer.tlsbunny.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class FileOutput extends AbstractOutput {

    private final Writer writer;
    private int index = 0;

    public FileOutput(Output output, String filename) {
        super(output);

        try {
            writer = new BufferedWriter(new FileWriter(filename));
        } catch (IOException e) {
            throw whatTheHell("could not create a file", e);
        }
    }

    @Override
    public void flush() {
        output.flush();

        try {
            List<String> lines = output.lines();
            for (;index < lines.size(); index++) {
                writer.write(lines.get(index));
            }
            writer.flush();
        } catch (IOException e) {
            throw whatTheHell("could not write to a file", e);
        }
    }

    @Override
    public void close() {
        flush();

        try {
            writer.close();
        } catch (IOException e) {
            throw whatTheHell("could not close file writer", e);
        }
    }
}
