package com.gypsyengineer.tlsbunny.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Output implements AutoCloseable {

    private static boolean onlyAchtungs = false;

    public static final String ansi_red = "\u001B[31m";
    public static final String ansi_reset = "\u001B[0m";
    public static final String tlsbunny = "[tlsbunny] ";
    public static final int indent_step = 4;

    private final List<String> strings = new ArrayList<>();
    private String prefix = tlsbunny;
    private int index = 0;
    private String indent = "";
    private final List<OutputListener> listeners = Collections.synchronizedList(new ArrayList<>());

    public static Output newOutput() {
        return new Output();
    }

    public static Output newOutput(String prefix) {
        return new Output(prefix);
    }

    public static void printOnlyAchtungs() {
        synchronized (Output.class) {
            onlyAchtungs = true;
        }
    }

    public static void printAll() {
        synchronized (Output.class) {
            onlyAchtungs = false;
        }
    }

    public static boolean onlyAchtungs() {
        synchronized (Output.class) {
            return onlyAchtungs;
        }
    }

    public Output() {

    }

    public Output(String prefix) {
        prefix(prefix);
    }

    public Output add(OutputListener listener) {
        listeners.add(listener);
        return this;
    }

    synchronized public void increaseIndent() {
        int indentLength = indent.length() + indent_step;
        indent = new String(new char[indentLength]).replace('\0', ' ');
    }

    synchronized public void decreaseIndent() {
        int indentLength = indent.length() - indent_step;

        if (indentLength < 0) {
            indentLength = 0;
        }

        indent = new String(new char[indentLength]).replace('\0', ' ');
    }

    synchronized private void printf(String format, Object... params) {
        strings.add(String.format(format, params));
    }

    synchronized public void prefix(String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            this.prefix = String.format("%s%s: ", tlsbunny, prefix);
        } else {
            this.prefix = String.format("%s: ", tlsbunny);
        }
    }

    synchronized public void info(String format, Object... values) {
        String text = String.format(format, values);
        String[] lines = text.split("\\r?\\n");
        for (OutputListener listener : listeners) {
            listener.receivedInfo(lines);
        }
        if (onlyAchtungs()) {
            return;
        }
        for (String line : lines) {
            printf("%s%s%s%n", prefix, indent, line);
        }
    }

    synchronized public void info(String message, Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos, true));
        info(String.format("%s%n%s", message, new String(baos.toByteArray())));
    }

    synchronized public void achtung(String format, Object... values) {
        String line = String.format(format, values);
        for (OutputListener listener : listeners) {
            listener.receivedAchtung(line);
        }
        printf("%s%sachtung: %s%s%n",
                prefix, ansi_red, line, ansi_reset);
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

    synchronized public List<String> strings() {
        return Collections.unmodifiableList(strings);
    }

    synchronized public boolean contains(String phrase) {
        for (String string : strings) {
            if (string.contains(phrase)) {
                return true;
            }
        }

        return false;
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
