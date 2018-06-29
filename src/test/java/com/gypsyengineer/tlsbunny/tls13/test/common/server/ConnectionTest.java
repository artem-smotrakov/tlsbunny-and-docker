package com.gypsyengineer.tlsbunny.tls13.test.common.server;

import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;
import org.junit.Test;

import java.io.IOException;

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
        }
    }

    private static class EchoServer extends SimpleServer {

        public EchoServer() throws IOException {
            super();
        }

        @Override
        protected void handle(Connection connection) throws IOException {
            output.info("[server] accepted");
            byte[] data = connection.read();
            output.info("[server] received: " + new String(data));
            connection.send(data);
        }
    }
}
