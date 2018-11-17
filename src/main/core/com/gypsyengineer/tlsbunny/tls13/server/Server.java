package com.gypsyengineer.tlsbunny.tls13.server;

import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.HasOutput;

public interface Server extends Runnable, AutoCloseable, HasOutput<Server> {

    int start_delay = 1000; // in millis

    Server set(Config config);
    Server set(EngineFactory engineFactory);

    // TODO it should accept multiple checks
    Server set(Check check);

    Server stopWhen(StopCondition condition);

    EngineFactory engineFactory();

    /**
     * Stops the server.
     */
    Server stop();

    /**
     * @return true if the server is running, false otherwise
     */
    boolean running();

    /**
     * @return the port number on which the server is running
     */
    int port();

    /**
     * @return all Engine instances which were used to handle connections
     */
    Engine[] engines();

    /**
     * @return false if the check failed at least once, true otherwise
     */
    boolean failed();

    /**
     * Starts the server in a new thread.
     *
     * @return the thread where the server is running
     */
    default Thread start() {
        Thread thread = new Thread(this);
        thread.start();

        try {
            Thread.sleep(start_delay);
        } catch (InterruptedException e) {
            output().achtung("exception: ", e);
        }

        return thread;
    }

    /**
     * Applies an analyzer to all engines in the server.
     */
    default Server apply(Analyzer analyzer) {
        for (Engine engine : engines()) {
            engine.apply(analyzer);
        }

        return this;
    }
}
