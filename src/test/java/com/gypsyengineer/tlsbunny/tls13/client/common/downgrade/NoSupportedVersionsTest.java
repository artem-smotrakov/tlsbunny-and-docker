package com.gypsyengineer.tlsbunny.tls13.client.common.downgrade;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.server.common.SingleThreadServer;
import com.gypsyengineer.tlsbunny.tls13.server.common.OneConnectionReceived;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.alert;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;
import static org.junit.Assert.*;

public class NoSupportedVersionsTest {

    @Test
    public void httpsClient() throws Exception {
        Output serverOutput = new Output("server");
        Output clientOutput = new Output("client");

        SingleThreadServer server = new SingleThreadServer()
                .set(new EngineFactoryImpl().set(serverOutput))
                .set(serverOutput)
                .stopWhen(new OneConnectionReceived());

        try (server; clientOutput; serverOutput) {
            server.start();

            NoSupportedVersions client = NoSupportedVersions.run(
                    clientOutput,
                    SystemPropertiesConfig.load().port(server.port()));

            server.await();
            server.recentEngine().run(new AlertCheck());

            Context clientContext = client.engine().context();
            Context serverContext = server.recentEngine().context();
            Alert alert = serverContext.getAlert();
            assertTrue(alert.isFatal());
            assertFalse(alert.isWarning());
            assertEquals(AlertLevel.fatal, alert.getLevel());
            assertNotEquals(AlertLevel.warning, alert.getLevel());
            assertEquals(AlertDescription.close_notify, alert.getDescription());
            assertNotEquals(AlertDescription.unexpected_message, alert.getDescription());

            StructParser parser = StructFactory.getDefault().parser();

            ClientHello clientHello = parser.parseClientHello(
                    serverContext.getFirstClientHello().getBody());
            assertNotNull(clientHello);
            assertEquals(ProtocolVersion.TLSv12, clientHello.getProtocolVersion());
            assertEquals(1, clientHello.getCipherSuites().size());
            assertEquals(CipherSuite.TLS_AES_128_GCM_SHA256, clientHello.getCipherSuites().first());
            assertEquals(1, clientHello.getLegacyCompressionMethods().size());
            assertEquals(CompressionMethod.None, clientHello.getLegacyCompressionMethods().first());
            assertNull(clientHello.findExtension(ExtensionType.supported_versions));

            ServerHello serverHello = parser.parseServerHello(
                    clientContext.getServerHello().getBody());
            assertNotNull(serverHello);
            assertEquals(ProtocolVersion.TLSv12, serverHello.getProtocolVersion());
            assertEquals(CipherSuite.TLS_AES_128_GCM_SHA256, serverHello.getCipherSuite());
            assertEquals(CompressionMethod.None, serverHello.getLegacyCompressionMethod());
            assertEquals(clientHello.getLegacySessionId(), serverHello.getLegacySessionIdEcho());

            Extension ext = serverHello.findExtension(ExtensionType.supported_versions);
            assertNotNull(ext);
            assertEquals(ExtensionType.supported_versions, ext.getExtensionType());
            SupportedVersions.ServerHello supportedVersions =
                    parser.parseSupportedVersionsServerHello(ext.getExtensionData().bytes());
            assertEquals(ProtocolVersion.TLSv12, supportedVersions.getSelectedVersion());
        }
    }

    private static class EngineFactoryImpl extends BaseEngineFactory {

        @Override
        public Engine create() throws NegotiatorException, NoSuchAlgorithmException {
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

                    // send ServerHello
                    .run(new GeneratingServerHello()
                            .supportedVersion(TLSv12)
                            .downgradeProtection(TLSv12)
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
