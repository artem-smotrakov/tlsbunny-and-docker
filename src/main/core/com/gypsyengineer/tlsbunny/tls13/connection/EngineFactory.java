package com.gypsyengineer.tlsbunny.tls13.connection;

public interface EngineFactory {

    Engine create() throws EngineException;
}
