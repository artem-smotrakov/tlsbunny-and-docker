package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.BaseEngineFactory;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.server.SingleThreadServer;
import com.gypsyengineer.tlsbunny.tls13.struct.AlertDescription;
import com.gypsyengineer.tlsbunny.tls13.struct.AlertLevel;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.struct.ChangeCipherSpec.MAX;
import static com.gypsyengineer.tlsbunny.tls13.struct.ChangeCipherSpec.MIN;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.alert;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvalidCCSTest {

    private static final int number_of_connections = MAX - MIN;

    @Test
    public void test() throws Exception {
        Output serverOutput = new Output("server");
        Output clientOutput = new Output("client");

        Config serverConfig = SystemPropertiesConfig.load();

        InvalidCCS client = new InvalidCCS();

        SingleThreadServer server = new SingleThreadServer()
                .set(new EngineFactoryImpl()
                        .set(serverConfig)
                        .set(serverOutput))
                .set(serverConfig)
                .set(serverOutput)
                .maxConnections(number_of_connections);

        try (server; clientOutput; serverOutput) {
            server.start();

            Config clientConfig = SystemPropertiesConfig.load().port(server.port());
            client.set(clientConfig).set(clientOutput);

            try (client) {
                client.connect();
            }
        }

        assertEquals(number_of_connections, client.engines().length);
        for (Engine engine : client.engines()) {
            assertTrue(engine.context().getAlert() != null
                    || engine.exception() != null);
        }

        assertEquals(number_of_connections, server.engines().length);
    }

    private static class EngineFactoryImpl extends BaseEngineFactory {

        public EngineFactoryImpl set(Config config) {
            this.config = config;
            return this;
        }

        @Override
        protected Engine createImpl() throws Exception {
            return Engine.init()
                    .set(structFactory)
                    .set(output)

                    .receive(new IncomingData())

                    // process ClientHello
                    .run(new ProcessingTLSPlaintext()
                            .expect(handshake))
                    .run(new ProcessingHandshake()
                            .expect(client_hello)
                            .updateContext(Context.Element.first_client_hello))
                    .run(new ProcessingClientHello())

                    // receive an invalid CCS
                    .receive(new IncomingChangeCipherSpec())

                    // send an alert
                    .run(new GeneratingAlert()
                            .level(AlertLevel.fatal)
                            .description(AlertDescription.unexpected_message))
                    .run(new WrappingIntoTLSPlaintexts()
                            .version(TLSv12)
                            .type(alert))
                    .send(new OutgoingData());
        }
    }
}
