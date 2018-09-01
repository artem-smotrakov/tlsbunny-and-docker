package com.gypsyengineer.tlsbunny.tls13.client.downgrade;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Side;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.server.common.SimpleServer;
import com.gypsyengineer.tlsbunny.tls13.struct.Alert;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.alert;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;
import static org.junit.Assert.*;

public class NoSupportedVersionsTest {

    private static final long delay = 1000; // in millis

    @Test
    public void httpsClient() throws Exception {
        Output serverOutput = new Output();
        Output clientOutput = new Output();
        serverOutput.prefix("server");
        clientOutput.prefix("client");

        ServerImpl server = new ServerImpl();
        server.set(SystemPropertiesConfig.load());
        server.set(serverOutput);
        server.maxConnections(1);

        try (server; clientOutput; serverOutput) {
            Thread thread = new Thread(server);
            thread.start();
            Thread.sleep(delay);

            Config clientConfig = SystemPropertiesConfig.load();
            clientConfig.port(server.port());

            NoSupportedVersions.run(clientOutput, clientConfig);
            server.await();
            server.engine().run(new AlertCheck());

            Context serverContext = server.engine().context();
            Alert alert = serverContext.getAlert();
            assertTrue(alert.isFatal());
            assertFalse(alert.isWarning());
        }
    }

    private static class ServerImpl extends SimpleServer {

        public ServerImpl() throws IOException {
            super();
        }

        @Override
        protected Engine createEngine()
                throws NegotiatorException, NoSuchAlgorithmException {

            return Engine.init()
                    .set(factory)
                    .set(output)

                    .receive(new IncomingData())

                    // process ClientHello
                    .run(new ProcessingTLSPlaintext()
                            .expect(handshake))
                    .run(new ProcessingHandshake()
                            .expect(client_hello)
                            .updateContext(Context.Element.first_client_hello))
                    .run(new ProcessingClientHello())

                    // send ServerHello
                    .run(new GeneratingServerHello()
                            .supportedVersion(TLSv12)
                            .downgradeTLSv12()
                            .group(secp256r1)
                            .signatureScheme(ecdsa_secp256r1_sha256)
                            .keyShareEntry(context -> context.negotiator.createKeyShareEntry()))
                    .run(new WrappingIntoHandshake()
                            .type(server_hello)
                            .updateContext(Context.Element.server_hello))
                    .run(new WrappingIntoTLSPlaintexts()
                            .type(handshake)
                            .version(TLSv12))
                    .send(new OutgoingData())

                    .receive(new IncomingData())

                    .run(new ProcessingTLSPlaintext()
                            .expect(alert))
                    .run(new ProcessingAlert());
        }
    }
}
