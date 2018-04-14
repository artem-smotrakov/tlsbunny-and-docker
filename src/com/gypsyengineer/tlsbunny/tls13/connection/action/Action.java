package com.gypsyengineer.tlsbunny.tls13.connection.action;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.nio.ByteBuffer;

public interface Action {
    String name();
    Action set(Output output);
    Action set(Context context);
    Action set(ByteBuffer buffer);
    Action run() throws Exception;
    boolean produced();
    ByteBuffer data();
}
