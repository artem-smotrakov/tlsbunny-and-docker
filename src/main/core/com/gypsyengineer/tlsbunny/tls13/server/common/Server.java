package com.gypsyengineer.tlsbunny.tls13.server.common;

import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

public interface Server extends Runnable, AutoCloseable {
    Server set(Config config);
    Server set(Output output);
    Server set(EngineFactory engineFactory);
    Server set(Check check);

    Server stopWhen(StopCondition condition);

    /**
     * Starts the server in a new thread.
     *
     * @return the thread where the server is running
     */
    Thread start();

    /**
     * @return the port number on which the server is running
     */
    int port();

    /**
     * @return the recent Engine instance which was used to handel a connection
     */
    Engine recentEngine();

    /**
     * Wait until the server stops.
     */
    void await();

    /**
     * @return false if the check failed at least once, true otherwise
     */
    boolean failed();
}
