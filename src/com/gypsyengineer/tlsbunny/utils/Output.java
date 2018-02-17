package com.gypsyengineer.tlsbunny.utils;

import java.util.ArrayList;
import java.util.List;

public class Output implements AutoCloseable {

    public static final String PREFIX = "[tlsbunny]";

    private final List<String> strings = new ArrayList<>();

    synchronized public void printf(String format, Object... params) {
        strings.add(String.format(format, params));
    }

    synchronized public void println(String string) {
        strings.add(string);
        strings.add("\n");
    }

    public void info(String format, Object... values) {
        printf("%s %s%n", PREFIX, String.format(format, values));
    }

    public void achtung(String format, Object... values) {
        printf("%s achtung: %s%n", PREFIX, String.format(format, values));
    }

    synchronized public void flush() {
        synchronized (System.out) {
            strings.forEach(string -> System.out.print(string));
        }
    }

    @Override
    public void close() throws Exception {
        flush();
    }

}