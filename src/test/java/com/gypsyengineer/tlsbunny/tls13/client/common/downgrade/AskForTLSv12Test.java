package com.gypsyengineer.tlsbunny.tls13.client.common.downgrade;

import com.gypsyengineer.tlsbunny.tls13.connection.AlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.server.common.SimpleServer;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.alert;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.client_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.server_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;
import static org.junit.Assert.*;

public class AskForTLSv12Test {

    private static final long delay = 1000; // in millis

    @Test
    public void httpsClient() throws Exception {
        Output serverOutput = new Output("server");
        Output clientOutput = new Output("client");

        SimpleServer server = new ServerImpl()
                .set(SystemPropertiesConfig.load())
                .set(serverOutput)
                .maxConnections(1);

        try (server; clientOutput; serverOutput) {
            new Thread(server).start();
            Thread.sleep(delay);

            AskForTLSv12 client = AskForTLSv12.run(
                    clientOutput,
                    SystemPropertiesConfig.load().port(server.port()));

            server.await();
            server.engine().run(new AlertCheck());

            Context clientContext = client.engine().context();
            Context serverContext = server.engine().context();
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
            assertEquals(CompressionMethod.zero, clientHello.getLegacyCompressionMethods().first());
            Extension ext = clientHello.findExtension(ExtensionType.supported_versions);
            assertNotNull(ext);
            assertEquals(ExtensionType.supported_versions, ext.getExtensionType());
            SupportedVersions.ClientHello clientHelloSupportedVersions =
                    parser.parseSupportedVersionsClientHello(ext.getExtensionData().bytes());
            assertEquals(1, clientHelloSupportedVersions.getVersions().size());
            assertEquals(TLSv12, clientHelloSupportedVersions.getVersions().first());

            ServerHello serverHello = parser.parseServerHello(
                    clientContext.getServerHello().getBody());
            assertNotNull(serverHello);
            assertEquals(ProtocolVersion.TLSv12, serverHello.getProtocolVersion());
            assertEquals(CipherSuite.TLS_AES_128_GCM_SHA256, serverHello.getCipherSuite());
            assertEquals(CompressionMethod.zero, serverHello.getLegacyCompressionMethod());
            assertEquals(clientHello.getLegacySessionId(), serverHello.getLegacySessionIdEcho());

            ext = serverHello.findExtension(ExtensionType.supported_versions);
            assertNotNull(ext);
            assertEquals(ExtensionType.supported_versions, ext.getExtensionType());
            SupportedVersions.ServerHello serverHelloSupportedVersions =
                    parser.parseSupportedVersionsServerHello(ext.getExtensionData().bytes());
            assertEquals(ProtocolVersion.TLSv12, serverHelloSupportedVersions.getSelectedVersion());
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
