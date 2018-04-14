package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;

public interface Check {
    String name();
    Check set(Engine engine);
    Check set(Context context);
    Check run();
    boolean failed();
}
