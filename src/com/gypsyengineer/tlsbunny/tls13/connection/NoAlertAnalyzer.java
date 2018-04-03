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
        int count = 0;
        for (Map.Entry<String, Context> entry : contexts.entrySet()) {
            String label = entry.getKey();
            Context context = entry.getValue();

            if (!context.hasAlert()) {
                output.info("connection '%s' didn't result to an alert", label);
                count++;
            }
        }

        if (count == 0) {
            output.info("all connections resulted to an alert");
        } else if (count == 1) {
            output.info("found 1 connection which didn't result to an alert");
        } else {
            output.info("found %d connections which didn't result to an alert", count);
        }

        return this;
    }

}
