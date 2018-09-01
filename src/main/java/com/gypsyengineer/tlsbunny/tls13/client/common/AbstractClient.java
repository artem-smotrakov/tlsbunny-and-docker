package com.gypsyengineer.tlsbunny.tls13.client.common;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

public abstract class AbstractClient implements Client, AutoCloseable {

    protected Config config = SystemPropertiesConfig.load();
    protected StructFactory factory = StructFactory.getDefault();
    protected Output output = new Output();
    protected Engine engine;

    public Config config() {
        return config;
    }

    @Override
    public Client set(Config config) {
        this.config = config;
        return this;
    }

    @Override
    public Client set(StructFactory factory) {
        this.factory = factory;
        return this;
    }

    @Override
    public Client set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public void close() {
        if (output != null) {
            output.flush();
        }
    }

    @Override
    public Engine engine() {
        if (engine == null) {
            throw new IllegalStateException(
                    "what the hell? engine not initialized! (null)");
        }

        return engine;
    }

    @Override
    public final Engine connect() throws Exception {
        output.info("connect to %s:%d", config.host(), config.port());
        engine = createEngine();
        return engine.connect();
    }

    protected abstract Engine createEngine() throws Exception;
}
