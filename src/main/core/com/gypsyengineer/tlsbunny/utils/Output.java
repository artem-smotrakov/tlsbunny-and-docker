package com.gypsyengineer.tlsbunny.utils;

import java.util.List;

public interface Output extends AutoCloseable {
    Output add(OutputListener listener);

    void increaseIndent();

    void decreaseIndent();

    void prefix(String prefix);

    void info(String format, Object... values);

    void info(String message, Throwable e);

    void achtung(String format, Object... values);

    void achtung(String message, Throwable e);

    void add(SimpleOutput output);

    List<String> strings();

    boolean contains(String phrase);

    void flush();

    @Override
    void close();
}
