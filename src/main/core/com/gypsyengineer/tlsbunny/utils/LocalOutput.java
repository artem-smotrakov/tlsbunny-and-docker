package com.gypsyengineer.tlsbunny.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LocalOutput implements Output {

    private static final String default_prefix = "";
    private static final int indent_step = 4;

    private final List<String> lines = new ArrayList<>();
    private String prefix = default_prefix;
    private String indent = "";
    private final List<OutputListener> listeners
            = Collections.synchronizedList(new ArrayList<>());

    public LocalOutput() {

    }

    public LocalOutput(String prefix) {
        prefix(prefix);
    }

    @Override
    public Output add(OutputListener listener) {
        listeners.add(listener);
        return this;
    }

    @Override
    synchronized public void increaseIndent() {
        int indentLength = indent.length() + indent_step;
        indent = new String(new char[indentLength]).replace('\0', ' ');
    }

    @Override
    synchronized public void decreaseIndent() {
        int indentLength = indent.length() - indent_step;

        if (indentLength < 0) {
            indentLength = 0;
        }

        indent = new String(new char[indentLength]).replace('\0', ' ');
    }

    synchronized private void printf(String format, Object... params) {
        lines.add(String.format(format, params));
    }

    @Override
    synchronized public void prefix(String prefix) {
        Objects.requireNonNull(prefix, "prefix can't be null!");
        this.prefix = prefix;
    }

    @Override
    synchronized public void info(String format, Object... values) {
        String text = format;
        if (values != null && values.length != 0) {
            text = String.format(format, values);
        }

        String[] lines = text.split("\\r?\\n");

        for (OutputListener listener : listeners) {
            listener.receivedInfo(lines);
        }

        for (String line : lines) {
            printf("%s%s%s%n", prefix, indent, line);
        }
    }

    @Override
    synchronized public void info(String message, Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos, true));
        info(String.format("%s%n%s", message, new String(baos.toByteArray())));
    }

    @Override
    synchronized public void achtung(String format, Object... values) {
        String line = String.format(format, values);
        for (OutputListener listener : listeners) {
            listener.receivedAchtung(line);
        }
        printf("%sachtung: %s%n", prefix, line);
    }

    @Override
    synchronized public void achtung(String message, Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos, true));
        achtung(String.format("%s%n%s", message, new String(baos.toByteArray())));
    }

    @Override
    synchronized public List<String> lines() {
        return Collections.unmodifiableList(lines);
    }

    @Override
    synchronized public boolean contains(String phrase) {
        for (String string : lines) {
            if (string.contains(phrase)) {
                return true;
            }
        }

        return false;
    }

    @Override
    synchronized public void flush() {
        // do nothing
    }

    @Override
    synchronized public void close() {
        flush();
    }

}
