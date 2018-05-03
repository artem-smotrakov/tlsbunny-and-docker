package com.gypsyengineer.tlsbunny.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Output implements AutoCloseable {

    public static final String TLSBUNNY = "[tlsbunny] ";
    public static final int INDENT_STEP = 4;

    private final List<String> strings = new ArrayList<>();
    private String prefix = TLSBUNNY;
    private int index = 0;
    private String indent = "";

    synchronized public void increaseIndent() {
        int indentLength = indent.length() + INDENT_STEP;
        indent = new String(new char[indentLength]).replace('\0', ' ');
    }

    synchronized public void decreaseIndent() {
        int indentLength = indent.length() - INDENT_STEP;

        if (indentLength < 0) {
            indentLength = 0;
        }

        indent = new String(new char[indentLength]).replace('\0', ' ');
    }

    synchronized public void printf(String format, Object... params) {
        strings.add(String.format(format, params));
    }

    synchronized public void prefix(String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            this.prefix = String.format("%s%s: ", TLSBUNNY, prefix);
        } else {
            this.prefix = String.format("%s: ", TLSBUNNY);
        }
    }

    public void info(String format, Object... values) {
        String text = String.format(format, values);
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            printf("%s%s%s%n", prefix, indent, line);
        }
    }

    synchronized public void achtung(String format, Object... values) {
        printf("%sachtung: %s%n", prefix, String.format(format, values));
    }

    synchronized public void achtung(String message, Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos, true));
        achtung(String.format("%s%n%s", message, new String(baos.toByteArray())));
    }

    synchronized public void add(Output output) {
        strings.addAll(output.strings);
    }

    synchronized public void reset() {
        index = 0;
    }

    synchronized public void flush() {
        synchronized (System.out) {
            while (index < strings.size()) {
                System.out.print(strings.get(index));
                index++;
            }
        }
    }

    @Override
    synchronized public void close() {
        flush();
    }

}
