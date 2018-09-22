package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

public interface Client extends AutoCloseable {
    Config config();
    Client set(Config config);
    Client set(StructFactory factory);
    Client set(Output output);
    Client set(Check... check);
    Client connect() throws Exception;
    Engine engine();
}
