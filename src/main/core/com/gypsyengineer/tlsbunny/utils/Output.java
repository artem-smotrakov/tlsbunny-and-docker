package com.gypsyengineer.tlsbunny.utils;

import java.util.List;

public interface Output extends AutoCloseable {

    static Output console() {
        return new ConsoleOutput(new OutputStorage());
    }

    static Output console(String prefix) {
        return new ConsoleOutput(new OutputStorage(prefix));
    }


    Output add(OutputListener listener);

    void increaseIndent();

    void decreaseIndent();

    void prefix(String prefix);

    void info(String format, Object... values);

    void info(String message, Throwable e);

    void achtung(String format, Object... values);

    void achtung(String message, Throwable e);

    List<String> lines();

    boolean contains(String line);

    void flush();

    @Override
    void close();
}
