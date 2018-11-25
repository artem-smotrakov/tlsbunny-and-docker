package com.gypsyengineer.tlsbunny.tls13.state;

public class Transition {

    private final State from;
    private final State to;

    public static Transition transition(State from, State to) {
        return new Transition(from, to);
    }

    private Transition(State from, State to) {
        this.from = from;
        this.to = to;
    }
}
