package com.gypsyengineer.tlsbunny.tls13.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class State {

    private final Map<String, Fact> facts = new HashMap<>();

    public State add(Fact fact) {
        Objects.requireNonNull(fact.name(), "hey! name can't be null!");
        facts.put(fact.name(), fact);
        return this;
    }

    public boolean contains(String name) {
        Objects.requireNonNull(name, "hey! name can't be null!");
        return facts.containsKey(name);
    }

    public Fact get(String name) {
        Objects.requireNonNull(name, "hey! name can't be null!");
        return facts.get(name);
    }

    public Fact[] facts() {
        Fact[] array = new Fact[facts.size()];

        int i = 0;
        for (Map.Entry<String, Fact> entry : facts.entrySet()) {
            array[i++] = entry.getValue();
        }

        return array;
    }
}
