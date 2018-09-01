package com.gypsyengineer.tlsbunny.tls13.server.common;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

public interface Server extends Runnable, AutoCloseable {
    Server set(Config config);
    Server set(StructFactory factory);
    Server set(Output output);
    int port();
    Engine engine();
}
