package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;

public abstract class AbstractCheck implements Check {

    protected Engine engine;
    protected Context context;

    protected boolean failed = true;

    @Override
    public Check set(Engine engine) {
        this.engine = engine;
        return this;
    }

    @Override
    public Check set(Context context) {
        this.context = context;
        return this;
    }

    @Override
    public boolean failed() {
        return failed;
    }

}
