package com.gypsyengineer.tlsbunny.tls13.client.common;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

public interface Client extends AutoCloseable {
    Client set(Config config);
    Client set(StructFactory factory);
    Client set(Output output);
    Engine connect() throws Exception;
    Engine engine();
}