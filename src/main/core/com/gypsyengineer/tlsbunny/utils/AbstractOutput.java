package com.gypsyengineer.tlsbunny.utils;

import java.util.List;

public abstract class AbstractOutput implements Output {

    protected final Output output;

    public AbstractOutput(Output output) {
        this.output = output;
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
    public List<String> lines() {
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
}
