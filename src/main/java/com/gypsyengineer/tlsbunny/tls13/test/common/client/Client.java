package com.gypsyengineer.tlsbunny.tls13.test.common.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

public interface Client {
    Client set(Config config);
    Client set(StructFactory factory);
    Client set(Output output);
    Engine connect() throws Exception;
}
