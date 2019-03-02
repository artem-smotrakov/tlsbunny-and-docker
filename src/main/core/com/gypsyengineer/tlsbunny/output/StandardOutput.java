package com.gypsyengineer.tlsbunny.output;

import java.util.List;

public class StandardOutput extends OutputWrapper {

    private static Level globalLevel = Level.valueOf(
            System.getProperty("tlsbunny.output.standard.level",
                    Level.info.name()));

    protected static final Object consoleLock = new Object();

    private static final String ansi_red = "\u001B[31m";
    private static final String ansi_reset = "\u001B[0m";
    private static final boolean enableHighlighting = Boolean.parseBoolean(
            System.getProperty("tlsbunny.output.enable.highlighting", "true"));

    protected int index = 0;

    public StandardOutput(Output output) {
        super(output, globalLevel);
    }

    @Override
    public void flush() {
        synchronized (consoleLock) {
            output.flush();

            List<Line> lines = output.lines();
            for (;index < lines.size(); index++) {
                Line line = lines.get(index);

                if (!line.printable(level)) {
                    continue;
                }

                String string = line.value();

                if (enableHighlighting && string.contains("achtung")) {
                    string = String.format("%s%s%s", ansi_red, string, ansi_reset);
                }

                System.out.println(string);
            }
        }
    }
}
