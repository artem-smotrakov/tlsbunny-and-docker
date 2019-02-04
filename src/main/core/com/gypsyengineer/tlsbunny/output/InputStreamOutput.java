package com.gypsyengineer.tlsbunny.output;

import java.io.*;
import java.util.*;

import static com.gypsyengineer.tlsbunny.output.Level.achtung;
import static com.gypsyengineer.tlsbunny.output.Level.info;

public class InputStreamOutput implements Output {

    private static final String default_prefix = "";

    private final List<Line> lines = new ArrayList<>();
    private String prefix = default_prefix;
    private final List<OutputListener> listeners
            = Collections.synchronizedList(new ArrayList<>());
    private InputStream is;

    public InputStreamOutput set(InputStream is) {
        this.is = new BufferedInputStream(is);
        return this;
    }

    @Override
    synchronized public Output add(OutputListener listener) {
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

    synchronized private void printf(Level level, String format, Object... params) {
        lines.add(new Line(level, String.format(format, params)));
    }

    @Override
    synchronized public void prefix(String prefix) {
        Objects.requireNonNull(prefix, "prefix can't be null!");
        if (prefix.isEmpty()) {
            this.prefix = prefix;
        } else {
            this.prefix = String.format("[%s] ", prefix);
        }
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
            printf(info, "%s%s%n", prefix, line);
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
        printf(achtung, "%sachtung: %s%n", prefix, line);
    }

    @Override
    synchronized public void achtung(String message, Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos, true));
        achtung(String.format("%s%n%s", message, new String(baos.toByteArray())));
    }

    @Override
    public void add(Line line) {
        lines.add(line);
    }

    @Override
    synchronized public List<Line> lines() {
        update();
        return Collections.unmodifiableList(lines);
    }

    @Override
    synchronized public boolean contains(String string) {
        for (Line line : lines) {
            if (line.contains(string)) {
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

    synchronized public InputStreamOutput update() {
        if (is != null) {
            info(read());
        }

        return this;
    }

    private String read() {
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

            return new String(baos.toByteArray());
        } catch (IOException e) {
            return "achtung: could not read from input stream!";
        }
    }
}
