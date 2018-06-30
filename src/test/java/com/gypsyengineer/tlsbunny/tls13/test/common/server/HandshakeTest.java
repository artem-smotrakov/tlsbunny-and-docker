package com.gypsyengineer.tlsbunny.tls13.test.common.server;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineException;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.Config;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.HttpsClient;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.client_hello;
import static org.junit.Assert.assertArrayEquals;

public class HandshakeTest {

    private static final long delay = 1000; // in millis
    private static final byte[] message =
            "like most of life's problems, this one can be solved with bending"
                    .getBytes();

    @Test
    public void basic() throws Exception {
        try (ServerImpl server = new ServerImpl();
             Output serverOutput = new Output();
             Output clientOutput = new Output()) {

            serverOutput.prefix("server");
            clientOutput.prefix("client");

            server.set(serverOutput);

            new Thread(server).start();
            Thread.sleep(delay);

            Config config = SystemPropertiesConfig.load();
            config.port(server.port());

            new HttpsClient()
                    .set(config)
                    .set(StructFactory.getDefault())
                    .set(clientOutput)
                    .connect()
                    .run(new NoAlertCheck());
        }
    }

    private static class ServerImpl extends SimpleServer {

        public ServerImpl() throws IOException {
            super();
        }

        @Override
        protected void handle(Connection connection)
                throws NegotiatorException, NoSuchAlgorithmException, EngineException {

            output.info("accepted");
            Engine.init()
                    .set(factory)
                    .set(output)
                    .set(connection)

                    .receive(new IncomingData())

                    // process ClientHello
                    .run(new ProcessingTLSPlaintext()
                            .expect(handshake))
                    .run(new ProcessingHandshake()
                            .expect(client_hello)
                            .updateContext(Context.Element.first_client_hello))
                    .run(new ProcessingClientHello())

                    .connect();
        }
    }
}
