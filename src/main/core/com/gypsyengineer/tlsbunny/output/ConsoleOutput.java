package com.gypsyengineer.tlsbunny.output;

import java.util.List;

public class ConsoleOutput extends AbstractOutput {

    private static Level level = Level.valueOf(
            System.getProperty("tlsbunny.output.console.level",
                    Level.info.name()));

    private static final Object consoleLock = new Object();

    private static final String ansi_red = "\u001B[31m";
    private static final String ansi_reset = "\u001B[0m";
    private static final boolean enableHighlighting = Boolean.valueOf(
            System.getProperty("tlsbunny.output.enable.highlighting", "true"));

    private static boolean onlyAchtung = Boolean.valueOf(
            System.getProperty("tlsbunny.output.only.achtung", "false"));

    private int index = 0;

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

                if (onlyAchtung && !string.contains("achtung")) {
                    continue;
                }

                if (enableHighlighting && string.contains("achtung")) {
                    string = String.format("%s%s%s", ansi_red, string, ansi_reset);
                }

                System.out.println(string);
            }
        }
    }
}
