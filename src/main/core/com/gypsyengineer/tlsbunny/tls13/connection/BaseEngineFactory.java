package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

public abstract class BaseEngineFactory implements EngineFactory {

    protected Config config = SystemPropertiesConfig.load();
    protected StructFactory structFactory = StructFactory.getDefault();
    protected Output output = new Output(String.format("output-%s-%d",
            BaseEngineFactory.class.getSimpleName(), System.currentTimeMillis()));

    public BaseEngineFactory set(Config config) {
        this.config = config;
        return this;
    }

    public BaseEngineFactory set(StructFactory factory) {
        structFactory = factory;
        return this;
    }

    public BaseEngineFactory set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public final Engine create() throws EngineException {
        try {
            return createImpl();
        } catch (Exception e) {
            throw new EngineException("could not create an engine", e);
        }
    }

    protected abstract Engine createImpl() throws Exception;

}
