package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Output;

public interface EngineFactory {
    EngineFactory set(StructFactory factory);
    EngineFactory set(Output output);

    StructFactory structFactory();
    Output output();

    Engine create() throws EngineException;
}
