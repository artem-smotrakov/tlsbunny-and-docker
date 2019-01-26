package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.OutputStorage;

public interface EngineFactory {
    EngineFactory set(StructFactory factory);
    EngineFactory set(OutputStorage output);

    StructFactory structFactory();
    Output output();

    Engine create() throws EngineException;
}
