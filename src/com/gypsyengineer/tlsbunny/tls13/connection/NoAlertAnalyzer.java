package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.HashMap;
import java.util.Map;

public class NoAlertAnalyzer implements Analyzer {

    private Output output;
    private final Map<String, Context> contexts = new HashMap<>();

    @Override
    public Analyzer set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public Analyzer add(String label, Context context) {
        if (contexts.containsKey(label)) {
            output.achtung("connection '%s' has been already added to %s",
                    label, NoAlertAnalyzer.class.getSimpleName());
        }
        contexts.put(label, context);
        return this;
    }

    @Override
    public Analyzer run() {
        output.info("let's look for connections with no alerts");
        return this;
    }

}
