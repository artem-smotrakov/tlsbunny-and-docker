package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface EngineFactory {

    // TODO: it should throw only EngineException (or something similar)
    Engine create() throws NegotiatorException, NoSuchAlgorithmException, IOException;
}
