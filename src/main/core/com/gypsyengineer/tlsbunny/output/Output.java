package com.gypsyengineer.tlsbunny.output;

import java.util.List;

public interface Output extends AutoCloseable {

    // global output level
    Level level = Level.valueOf(
            System.getProperty("tlsbunny.output.level", Level.info.name()));

    static Output standard() {
        return new StandardOutput(local());
    }

    static Output standard(String prefix) {
        return new StandardOutput(local(prefix));
    }

    static Output standardClient() {
        return standard("client");
    }

    static Output local() {
        return new LocalOutput();
    }

    static Output local(String prefix) {
        return new LocalOutput(prefix);
    }

    Output add(OutputListener listener);

    void increaseIndent();

    void decreaseIndent();

    void prefix(String prefix);

    void info(String format, Object... values);

    void info(String message, Throwable e);

    void important(String format, Object... values);

    void important(String message, Throwable e);

    void achtung(String format, Object... values);

    void achtung(String message, Throwable e);

    void add(Line line);

    List<Line> lines();

    boolean contains(String line);

    void clear();

    void flush();

    @Override
    void close();
}
