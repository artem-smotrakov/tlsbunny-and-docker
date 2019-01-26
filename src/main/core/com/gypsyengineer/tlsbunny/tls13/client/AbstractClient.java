package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public abstract class AbstractClient implements Client, AutoCloseable {

    protected Config config = SystemPropertiesConfig.load();
    protected StructFactory factory = StructFactory.getDefault();
    protected Negotiator negotiator;
    protected Output output = Output.console();
    protected Analyzer analyzer;
    protected List<Engine> engines = new ArrayList<>();
    protected List<Check> checks = Collections.emptyList();

    private boolean running = false;

    public AbstractClient() {
        try {
            negotiator = Negotiator.create(NamedGroup.secp256r1, StructFactory.getDefault());
        } catch (NegotiatorException e) {
            throw whatTheHell("could not create a negotiator!", e);
        }
    }

    @Override
    // TODO should it run checks and analyzers? should they be private?
    //      if so, connectImpl should probably return engines
    public final Client connect() throws Exception {
        synchronized (this) {
            running = true;
            engines = new ArrayList<>();
        }
        try {
            return connectImpl();
        } finally {
            synchronized (this) {
                running = false;
                if (analyzer != null) {
                    analyzer.add(engines.toArray(new Engine[0]));
                }
            }
        }
    }

    protected abstract Client connectImpl() throws Exception;

    @Override
    synchronized public boolean running() {
        return running;
    }

    @Override
    public Output output() {
        return output;
    }

    @Override
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
    public Client set(Negotiator negotiator) {
        this.negotiator = negotiator;
        return this;
    }

    @Override
    public Client set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public Client set(Check... checks) {
        this.checks = List.of(checks != null ? checks : no_checks);
        return this;
    }

    @Override
    public Client set(Analyzer analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    @Override
    public void close() {
        if (output != null) {
            output.flush();
        }
    }

    @Override
    public Engine[] engines() {
        return engines.toArray(new Engine[0]);
    }

}
