package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.HasOutput;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public interface Client extends AutoCloseable, Runnable, HasOutput<Client> {

    Check[] no_checks = new Check[0];

    Config config();
    Client set(Config config);
    Client set(StructFactory factory);
    Client set(Negotiator negotiator);
    Client set(Output output);
    Client set(Check... checks);
    Client set(Analyzer analyzer);
    Client connect() throws Exception;

    Engine[] engines();

    default void run() {
        try {
            connect();
        } catch (Exception e) {
            throw whatTheHell("exception on client side", e);
        }
    }

    /**
     * Starts the client in a new thread.
     *
     * @return the thread where the client is running
     */
    default Thread start() {
        Thread thread = new Thread(this);
        thread.start();
        return thread;
    }

    /**
     * Applies an analyzer to all engines in the client.
     */
    // TODO: we also have set(Analyzer) - do we need it?
    default Client apply(Analyzer analyzer) {
        for (Engine engine : engines()) {
            engine.apply(analyzer);
        }

        return this;
    }

    /**
     * Stops the client.
     */
    default Client stop() {
        throw new UnsupportedOperationException("no stopping for you!");
    }

    /**
     * @return true if the client is running, false otherwise
     */
    default boolean running() {
        throw new UnsupportedOperationException("no running for you!");
    }
}
