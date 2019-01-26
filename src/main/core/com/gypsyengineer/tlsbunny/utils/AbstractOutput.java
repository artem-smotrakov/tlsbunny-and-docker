package com.gypsyengineer.tlsbunny.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractOutput implements Output {

    protected final Output output;

    private final List<OutputListener> listeners
            = Collections.synchronizedList(new ArrayList<>());

    public AbstractOutput(Output output) {
        this.output = output;
    }

    @Override
    synchronized public Output add(OutputListener listener) {
        listeners.add(listener);
        return this;
    }

    @Override
    synchronized public void increaseIndent() {
        output.increaseIndent();
    }

    @Override
    synchronized public void decreaseIndent() {
        output.decreaseIndent();
    }

    @Override
    synchronized public void prefix(String prefix) {
        output.prefix(prefix);
    }

    @Override
    synchronized public void info(String format, Object... values) {
        output.info(format, values);
    }

    @Override
    synchronized public void info(String message, Throwable e) {
        output.info(message, e);
    }

    @Override
    synchronized public void achtung(String format, Object... values) {
        output.achtung(format, values);
    }

    @Override
    synchronized public void achtung(String message, Throwable e) {
        output.achtung(message, e);
    }

    @Override
    synchronized public List<String> lines() {
        return output.lines();
    }

    @Override
    synchronized public boolean contains(String line) {
        return output.contains(line);
    }

    @Override
    synchronized public void flush() {
        output.flush();
    }

    @Override
    synchronized public void close() {
        output.close();
    }
}
