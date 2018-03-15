package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.action.Action;
import com.gypsyengineer.tlsbunny.tls13.analysis.Analyzer;

public class TLSConnection {

    public TLSConnection() {

    }

    public TLSConnection required(Action action) {
        return this;
    }

    public TLSConnection optional(Action action) {
        return this;
    }

    public TLSConnection run() {
        return this;
    }

    public TLSConnection analyze(Analyzer analyzer) {
        return this;
    }

}
