package com.gypsyengineer.tlsbunny.tls13.connection.action;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.nio.ByteBuffer;

public interface Action {
    String name();
    Action set(Output output);
    Action set(Context context);
    Action run() throws Exception;

    Action in(ByteBuffer buffer);
    ByteBuffer out();

    Action applicationData(ByteBuffer buffer);
    ByteBuffer applicationData();
}
