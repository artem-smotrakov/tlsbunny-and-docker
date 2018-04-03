package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.HashMap;
import java.util.Map;

public class NoAlertAnalyzer implements Analyzer {

    private Output output;
    private final Map<String, Holder> contexts = new HashMap<>();

    @Override
    public Analyzer set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public Analyzer add(String label, Context context) {
        Holder holder = contexts.get(label);
        if (holder == null) {
            holder = new Holder();
            contexts.put(label, holder);
        }
        holder.context = context;
        return this;
    }

    @Override
    public Analyzer add(String label, Output output) {
        Holder holder = contexts.get(label);
        if (holder == null) {
            holder = new Holder();
            contexts.put(label, holder);
        }
        holder.output = output;
        return this;
    }

    @Override
    public Analyzer run() {
        output.info("let's look for connections with no alerts");
        int count = 0;
        for (Map.Entry<String, Holder> entry : contexts.entrySet()) {
            String label = entry.getKey();
            Holder holder = entry.getValue();

            if (!holder.context.hasAlert()) {
                output.info("connection '%s' didn't result to an alert:", label);
                output.add(holder.output);
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

    private static class Holder {
        Output output;
        Context context;
    }

}
