package com.gypsyengineer.tlsbunny.tls13.server;

import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class SingleThreadServer implements Server {

    public static final int free_port = 0;

    private final ServerSocket serverSocket;

    // TODO: add synchronization
    private Config config = SystemPropertiesConfig.load();
    private EngineFactory factory;
    private StopCondition stopCondition = new NonStop();
    private Output output = new Output("server");
    private Check check;
    private boolean failed = false;
    private boolean running = false;

    private List<Engine> engines = Collections.synchronizedList(new ArrayList<>());

    public SingleThreadServer() throws IOException {
        this(free_port);
    }

    public SingleThreadServer(int port) throws IOException {
        this(new ServerSocket(port));
    }

    private SingleThreadServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public Output output() {
        return output;
    }

    public SingleThreadServer maxConnections(int n) {
        stopCondition = new NConnectionsReceived(n);
        return this;
    }

    @Override
    public SingleThreadServer set(Config config) {
        this.config = config;
        return this;
    }

    @Override
    public SingleThreadServer set(EngineFactory factory) {
        this.factory = factory;
        return this;
    }

    @Override
    public SingleThreadServer set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public SingleThreadServer set(Check check) {
        this.check = check;
        return this;
    }

    @Override
    public SingleThreadServer stopWhen(StopCondition condition) {
        stopCondition = condition;
        return this;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public Engine[] engines() {
        return engines.toArray(new Engine[0]);
    }

    @Override
    public int port() {
        return serverSocket.getLocalPort();
    }

    @Override
    public void run() {
        if (factory == null) {
            throw whatTheHell("engine factory is not set! (null)");
        }

        output.info("started on port %d", port());
        running = true;
        while (stopCondition.shouldRun()) {
            try (Connection connection = Connection.create(serverSocket.accept())) {
                output.info("accepted");

                Engine engine = factory.create();
                engines.add(engine);

                engine.set(output);
                engine.set(connection);
                engine.connect(); // TODO: rename connect -> run

                if (check != null) {
                    output.info("run check: %s", check.name());
                    check.set(engine);
                    check.set(engine.context());
                    check.run();
                    failed &= check.failed();
                }

                output.info("done");
            } catch (Exception e) {
                output.achtung("exception: ", e);
                failed = true;
                break;
            }
        }

        running = false;
        output.info("stopped");
    }

    @Override
    public boolean running() {
        return running;
    }

    @Override
    public SingleThreadServer stop() {
        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                output.achtung("exception occurred while stopping the server", e);
            }
        }

        return this;
    }

    @Override
    public void close() throws IOException {
        if (!serverSocket.isClosed()) {
            serverSocket.close();
        }

        if (output != null) {
            output.flush();
        }
    }

}
