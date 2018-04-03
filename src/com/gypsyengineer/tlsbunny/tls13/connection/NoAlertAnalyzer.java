package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.ArrayList;
import java.util.List;

public class NoAlertAnalyzer implements Analyzer {

    private Output output;
    private final List<Context> contexts = new ArrayList<>();

    @Override
    public Analyzer set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public Analyzer add(Context context) {
        contexts.add(context);
        return this;
    }

    @Override
    public Analyzer run() {
        output.info("let's look for connections with no alerts");
        return this;
    }

}
