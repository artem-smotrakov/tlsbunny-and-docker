package com.gypsyengineer.tlsbunny.utils;

import java.util.Collections;
import java.util.List;

public class DummyOutput implements Output {

    @Override
    public Output add(OutputListener listener) {
        return this;
    }

    @Override
    public void increaseIndent() {

    }

    @Override
    public void decreaseIndent() {

    }

    @Override
    public void prefix(String prefix) {

    }

    @Override
    public void info(String format, Object... values) {

    }

    @Override
    public void info(String message, Throwable e) {

    }

    @Override
    public void achtung(String format, Object... values) {

    }

    @Override
    public void achtung(String message, Throwable e) {

    }

    @Override
    public List<String> lines() {
        return Collections.emptyList();
    }

    @Override
    public boolean contains(String line) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }
}
