package com.gypsyengineer.tlsbunny.tls13.test.common.client;

import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

public abstract class AbstractClient implements Client {

    protected Config config;
    protected StructFactory factory;
    protected Output output;

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
}
