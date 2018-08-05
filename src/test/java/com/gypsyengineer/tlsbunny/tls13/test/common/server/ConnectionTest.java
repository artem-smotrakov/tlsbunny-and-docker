package com.gypsyengineer.tlsbunny.tls13.test.common.server;

import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.assertArrayEquals;

public class ConnectionTest {

    private static final long delay = 1000; // in millis
    private static final byte[] message =
            "like most of life's problems, this one can be solved with bending"
                    .getBytes();

    @Test
    public void basic() throws IOException, InterruptedException {
        try (Output output = new Output(); EchoServer server = new EchoServer()) {
            server.set(output);

            new Thread(server).start();
            Thread.sleep(delay);

            output.info("[client] connect");
            Connection connection = Connection.create("localhost", server.port());
            connection.send(message);
            byte[] data = connection.read();
            output.info("[client] received: " + new String(data));

            assertArrayEquals(message, data);
        }
    }

    private static class EchoServer implements Runnable, AutoCloseable {

        private static final int FREE_PORT = 0;

        private Output output = new Output();
        private final ServerSocket serverSocket;

        public EchoServer() throws IOException {
            this(FREE_PORT);
        }

        public EchoServer(int port) throws IOException {
            this(new ServerSocket(port));
        }

        private EchoServer(ServerSocket ssocket) {
            this.serverSocket = ssocket;
        }

        public EchoServer set(Output output) {
            this.output = output;
            return this;
        }

        public int port() {
            return serverSocket.getLocalPort();
        }

        @Override
        public void run() {
            output.info("server started on port %d", port());
            while (true) {
                try (Connection connection = Connection.create(serverSocket.accept())) {
                    output.info("[server] accepted");
                    byte[] data = connection.read();
                    output.info("[server] received: " + new String(data));
                    connection.send(data);
                } catch (Exception e) {
                    output.achtung("exception: ", e);
                    break;
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
}
