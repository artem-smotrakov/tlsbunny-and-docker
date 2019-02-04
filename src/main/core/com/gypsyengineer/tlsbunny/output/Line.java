package com.gypsyengineer.tlsbunny.output;

public class Line {

    private final Level level;
    private final String value;

    public Line(Level level, String value) {
        this.level = level;
        this.value = value;
    }

    public Level level() {
        return level;
    }

    public String value() {
        return value;
    }

    public boolean printable(Level level) {
        return this.level.compareTo(level) >= 0;
    }

    public boolean contains(String string) {
        return value.contains(string);
    }
}
