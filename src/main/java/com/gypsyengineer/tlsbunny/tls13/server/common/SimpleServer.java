package com.gypsyengineer.tlsbunny.tls13.server.common;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.net.ServerSocket;

public abstract class SimpleServer implements Server {

    private static final int FREE_PORT = 0;
    private static final int delay = 500; // in millis

    protected Config config = SystemPropertiesConfig.load();
    protected StructFactory factory = StructFactory.getDefault();
    protected Output output = new Output();
    protected Engine engine;

    private final ServerSocket serverSocket;

    // TODO: add synchronization
    private boolean running = false;
    private int maxConnections = 1;
    private int connections = 0;

    public SimpleServer() throws IOException {
        this(FREE_PORT);
    }

    public SimpleServer(int port) throws IOException {
        this(new ServerSocket(port));
    }

    private SimpleServer(ServerSocket ssocket) {
        this.serverSocket = ssocket;
    }

    public SimpleServer maxConnections(int n) {
        maxConnections = n;
        return this;
    }

    @Override
    public Engine engine() {
        return engine;
    }

    @Override
    public SimpleServer set(Config config) {
        this.config = config;
        return this;
    }

    @Override
    public SimpleServer set(StructFactory factory) {
        this.factory = factory;
        return this;
    }

    @Override
    public SimpleServer set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public int port() {
        return serverSocket.getLocalPort();
    }

    @Override
    public void run() {
        output.info("server started on port %d", port());

        running = true;
        while (shouldRun()) {
            try (Connection connection = Connection.create(serverSocket.accept())) {
                output.info("accepted");
                engine = createEngine();
                engine.set(connection);
                engine.connect();
                output.info("done");
            } catch (Exception e) {
                output.achtung("exception: ", e);
                break;
            }
        }

        running = false;
        output.info("server stopped");
    }

    protected abstract Engine createEngine() throws Exception;

    public void await() {
        while (running) {
            try {
                Thread.sleep(delay);
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

    private boolean shouldRun() {
        connections++;
        if (maxConnections < 0) {
            return true;
        }

        return connections <= maxConnections;
    }

}
