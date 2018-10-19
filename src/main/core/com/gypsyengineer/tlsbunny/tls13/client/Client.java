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

    // TODO: should engine() be removed? or renamed ot lastEngine/recentEngine?
    Engine engine();
    Engine[] engines();

    default void run() {
        try {
            connect();
        } catch (Exception e) {
            throw whatTheHell("exception on client side", e);
        }
    }
}
