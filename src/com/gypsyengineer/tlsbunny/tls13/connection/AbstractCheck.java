package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;

public abstract class AbstractCheck implements Check {

    Engine connection;
    boolean failed = true;
    Context context;

    @Override
    public Check set(Engine connection) {
        this.connection = connection;
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
