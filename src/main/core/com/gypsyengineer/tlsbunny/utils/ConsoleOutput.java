package com.gypsyengineer.tlsbunny.utils;

import java.util.List;

public class ConsoleOutput extends AbstractOutput {

    private static final Object consoleLock = new Object();

    private static final String ansi_red = "\u001B[31m";
    private static final String ansi_reset = "\u001B[0m";
    private static final boolean enableHighlighting = Boolean.valueOf(
            System.getProperty("tlsbunny.output.enable.highlighting", "true"));

    private static boolean onlyAchtung = Boolean.valueOf(
            System.getProperty("tlsbunny.output.only.achtung", "false"));

    private int index = 0;

    public ConsoleOutput(Output output) {
        super(output);
    }

    @Override
    public void flush() {
        synchronized (consoleLock) {
            output.flush();

            List<String> lines = output.lines();
            for (;index < lines.size(); index++) {
                String string = lines.get(index);

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
