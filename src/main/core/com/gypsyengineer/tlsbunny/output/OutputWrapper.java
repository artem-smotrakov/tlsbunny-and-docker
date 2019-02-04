package com.gypsyengineer.tlsbunny.output;

import java.util.List;

public abstract class OutputWrapper implements Output {

    protected final Output output;
    protected final Level level;

    public OutputWrapper(Output output) {
        this(output, Output.level);
    }

    public OutputWrapper(Output output, Level level) {
        this.output = output;
        this.level = level;
    }

    @Override
    synchronized public Output add(OutputListener listener) {
        output.add(listener);
        return this;
    }

    @Override
    public void increaseIndent() {
        output.increaseIndent();
    }

    @Override
    public void decreaseIndent() {
        output.decreaseIndent();
    }

    @Override
    public void prefix(String prefix) {
        output.prefix(prefix);
    }

    @Override
    public void info(String format, Object... values) {
        output.info(format, values);
    }

    @Override
    public void info(String message, Throwable e) {
        output.info(message, e);
    }

    @Override
    public void achtung(String format, Object... values) {
        output.achtung(format, values);
    }

    @Override
    public void achtung(String message, Throwable e) {
        output.achtung(message, e);
    }

    @Override
    public List<Line> lines() {
        return output.lines();
    }

    @Override
    public boolean contains(String line) {
        return output.contains(line);
    }

    @Override
    public void flush() {
        output.flush();
    }

    @Override
    public void close() {
        output.close();
    }

    @Override
    public void add(Line line) {
        output.add(line);
    }

}
