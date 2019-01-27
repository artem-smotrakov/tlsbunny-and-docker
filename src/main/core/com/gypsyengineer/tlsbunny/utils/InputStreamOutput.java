package com.gypsyengineer.tlsbunny.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class InputStreamOutput implements Output {

    private static final String default_prefix = "";

    private final List<String> lines = new ArrayList<>();
    private String prefix = default_prefix;
    private final List<OutputListener> listeners
            = Collections.synchronizedList(new ArrayList<>());
    private InputStream is;

    public InputStreamOutput set(InputStream is) {
        this.is = is;
        return this;
    }

    @Override
    public Output add(OutputListener listener) {
        listeners.add(listener);
        return this;
    }

    @Override
    public void increaseIndent() {
        // do nothing
    }

    @Override
    public void decreaseIndent() {
        // do nothing
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
        String text = String.format(format, values);
        String[] lines = text.split("\\r?\\n");

        for (OutputListener listener : listeners) {
            listener.receivedInfo(lines);
        }

        for (String line : lines) {
            printf("%s%s%n", prefix, line);
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
    public List<String> lines() {
        update();
        return Collections.unmodifiableList(lines);
    }

    @Override
    public boolean contains(String line) {
        for (String string : lines) {
            if (string.contains(line)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void flush() {
        // do nothing
    }

    @Override
    public void close() {
        // do nothing
    }

    public InputStreamOutput update() {
        for (String line : read()) {
            info(line);
        }

        return this;
    }

    private List<String> read() {
        if (is == null) {
            return Collections.emptyList();
        }

        try {
            byte[] bytes = new byte[4096];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (is.available() > 0) {
                int len = is.read(bytes);
                if (len < 0) {
                    break;
                }
                baos.write(bytes, 0, len);
            }

            return Arrays.asList(new String(baos.toByteArray()).split("\\r?\\n"));
        } catch (IOException e) {
            return List.of("achtung: could not read from input stream!");
        }
    }
}