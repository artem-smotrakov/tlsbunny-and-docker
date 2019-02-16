package com.gypsyengineer.tlsbunny.output;

import java.util.List;

public class ConsoleOutput extends OutputWrapper {

    protected static Level level = Level.valueOf(
            System.getProperty("tlsbunny.output.console.level",
                    Level.info.name()));
    static {
        System.out.printf("[output] tlsbunny.output.console.level = %s%n", level);
    }

    protected static final Object consoleLock = new Object();

    private static final String ansi_red = "\u001B[31m";
    private static final String ansi_reset = "\u001B[0m";
    private static final boolean enableHighlighting = Boolean.valueOf(
            System.getProperty("tlsbunny.output.enable.highlighting", "true"));

    protected int index = 0;

    public ConsoleOutput() {
        this(new LocalOutput());
    }

    public ConsoleOutput(Output output) {
        super(output, level);
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
