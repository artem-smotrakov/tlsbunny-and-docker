package com.gypsyengineer.tlsbunny.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Output implements AutoCloseable {

    public static final String TLSBUNNY = "[tlsbunny]";

    private final List<String> strings = new ArrayList<>();
    private String prefix = "";
    private int index = 0;

    synchronized public void printf(String format, Object... params) {
        strings.add(String.format(format, params));
    }

    synchronized public void prefix(String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            this.prefix = String.format("%s %s:", TLSBUNNY, prefix);
        } else {
            this.prefix = String.format("%s:", TLSBUNNY);
        }
    }

    public void info(String format, Object... values) {
        printf("%s %s%n", prefix, String.format(format, values));
    }

    public void achtung(String format, Object... values) {
        printf("%s achtung: %s%n", prefix, String.format(format, values));
    }

    public void achtung(String message, Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos, true));
        achtung(String.format("%s%n%s", message, new String(baos.toByteArray())));
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
    public void close() {
        flush();
    }

}
