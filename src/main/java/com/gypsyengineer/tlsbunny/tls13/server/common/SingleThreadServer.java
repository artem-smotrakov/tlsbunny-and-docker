package com.gypsyengineer.tlsbunny.tls13.server.common;

import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.net.ServerSocket;

public class SingleThreadServer implements Server {

    private static final int FREE_PORT = 0;
    private static final int start_delay = 1000; // in millis
    private static final int wait_delay = 500; // in millis

    private final ServerSocket serverSocket;

    private Config config = SystemPropertiesConfig.load();
    private EngineFactory factory;
    private StopCondition stopCondition = new NonStop();
    private Output output = new Output("server");
    private Check check;

    private boolean failed = false;

    // TODO: add synchronization
    private Engine recentEngine;

    // TODO: add synchronization
    private boolean running = false;

    public SingleThreadServer() throws IOException {
        this(FREE_PORT);
    }

    public SingleThreadServer(int port) throws IOException {
        this(new ServerSocket(port));
    }

    private SingleThreadServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
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
    public Engine recentEngine() {
        return recentEngine;
    }

    @Override
    public int port() {
        return serverSocket.getLocalPort();
    }

    @Override
    public void run() {
        if (factory == null) {
            throw new IllegalStateException(
                    "what the hell? engine factory is not set! (null)");
        }

        output.info("started on port %d", port());
        running = true;
        while (stopCondition.shouldRun()) {
            try (Connection connection = Connection.create(serverSocket.accept())) {
                output.info("accepted");
                recentEngine = factory.create();
                recentEngine.set(output);
                recentEngine.set(connection);
                recentEngine.connect(); // TODO: rename connect -> run
                if (check != null) {
                    output.info("run check: %s", check.name());
                    check.set(recentEngine);
                    check.set(recentEngine.context());
                    check.run();
                    failed &= check.failed();
                }
                output.info("done");
            } catch (Exception e) {
                output.achtung("exception: ", e);
                break;
            }
        }

        running = false;
        output.info("stopped");
    }

    @Override
    public Thread start() {
        Thread thread = new Thread(this);
        thread.start();

        try {
            Thread.sleep(start_delay);
        } catch (InterruptedException e) {
            output.achtung("exception: ", e);
        }

        return thread;
    }

    @Override
    public void await() {
        while (running) {
            try {
                Thread.sleep(wait_delay);
            } catch (InterruptedException e) {
                output.achtung("exception occurred while waiting", e);
            }
        }
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
