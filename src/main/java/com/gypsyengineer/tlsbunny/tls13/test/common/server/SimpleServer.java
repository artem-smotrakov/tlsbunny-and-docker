package com.gypsyengineer.tlsbunny.tls13.test.common.server;

import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.Config;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.net.ServerSocket;

public abstract class SimpleServer implements Server {

    private static final int FREE_PORT = 0;

    protected Config config;
    protected StructFactory factory;
    protected Output output;

    private final ServerSocket serverSocket;

    public SimpleServer() throws IOException {
        this(FREE_PORT);
    }

    public SimpleServer(int port) throws IOException {
        this(new ServerSocket(port));
    }

    private SimpleServer(ServerSocket ssocket) {
        this.serverSocket = ssocket;
    }

    @Override
    public Server set(Config config) {
        this.config = config;
        return this;
    }

    @Override
    public Server set(StructFactory factory) {
        this.factory = factory;
        return this;
    }

    @Override
    public Server set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public int port() {
        return serverSocket.getLocalPort();
    }

    @Override
    public void run() {
        while (true) {
            try (Connection connection = Connection.create(serverSocket.accept())) {
                handle(connection);
            } catch (Exception e) {
                output.achtung("exception: ", e);
                break;
            }
        }
    }

    protected abstract void handle(Connection connection) throws Exception;

    @Override
    public void close() throws IOException {
        if (!serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

}
