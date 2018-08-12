package com.gypsyengineer.tlsbunny.tls13.test.common.server;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Phase;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Side;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.Config;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.Client;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.HttpsClient;
import com.gypsyengineer.tlsbunny.tls13.test.openssl.client.AnotherHttpsClient;
import com.gypsyengineer.tlsbunny.utils.Output;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.application_data;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv13_draft_26;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HandshakeTest {

    private static final long delay = 1000; // in millis
    private static final String serverCertificatePath = "certs/server_cert.der";
    private static final String serverKeyPath = "certs/server_key.pkcs8";

    @Test
    public void httpsClient() throws Exception {
        Config serverConfig = SystemPropertiesConfig.load();
        serverConfig.serverCertificate(serverCertificatePath);
        serverConfig.serverKey(serverKeyPath);

        boolean success = false;

        Client client = new HttpsClient()
                .set(StructFactory.getDefault());

        ServerImpl server = new ServerImpl(serverConfig);

        Output serverOutput = new Output();
        Output clientOutput = new Output();
        serverOutput.prefix("server");
        clientOutput.prefix("client");

        server.set(serverOutput);

        try (server; clientOutput; serverOutput) {
            new Thread(server).start();
            Thread.sleep(delay);

            Config clientConfig = SystemPropertiesConfig.load();
            clientConfig.port(server.port());

            client.set(clientConfig).set(clientOutput);

            try (client) {
                client.connect()
                        .run(new NoAlertCheck())
                        .run(new SuccessCheck())
                        .run(new NoExceptionCheck())
                        .apply(new NoAlertAnalyzer());
                success = true;
            } catch (Exception e) {
                clientOutput.achtung(
                        "client failed with an unexpected exception", e);
            }
        }

        success &= checkContexts(
                client.engine().context(),
                server.engine().context(),
                clientOutput);

        assertTrue("something went wrong!", success);
    }

    @Test
    public void anotherHttpsClient() throws Exception {
        Config serverConfig = SystemPropertiesConfig.load();
        serverConfig.serverCertificate(serverCertificatePath);
        serverConfig.serverKey(serverKeyPath);

        boolean success = false;

        Client client = new AnotherHttpsClient()
                .set(StructFactory.getDefault());

        ServerImpl server = new ServerImpl(serverConfig);

        Output serverOutput = new Output();
        Output clientOutput = new Output();
        serverOutput.prefix("server");
        clientOutput.prefix("client");

        server.set(serverOutput);

        try (server; clientOutput; serverOutput) {
            new Thread(server).start();
            Thread.sleep(delay);

            Config clientConfig = SystemPropertiesConfig.load();
            clientConfig.port(server.port());

            client.set(clientConfig).set(clientOutput);

            try (client) {
                client.connect()
                        .run(new NoAlertCheck())
                        .run(new SuccessCheck())
                        .run(new NoExceptionCheck())
                        .apply(new NoAlertAnalyzer());
                success = true;
            } catch (Exception e) {
                clientOutput.achtung(
                        "client failed with an unexpected exception", e);
            }
        }

        success &= checkContexts(
                client.engine().context(),
                server.engine().context(),
                clientOutput);

        assertTrue("something went wrong!", success);
    }

    private static class ServerImpl extends SimpleServer {

        private final Config config;

        public ServerImpl(Config config) throws IOException {
            super();
            this.config = config;
        }

        @Override
        protected Engine createEngine()
                throws NegotiatorException, NoSuchAlgorithmException, IOException {

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
                            .supportedVersion(TLSv13_draft_26)
                            .group(secp256r1)
                            .signatureScheme(ecdsa_secp256r1_sha256)
                            .keyShareEntry(context -> context.negotiator.createKeyShareEntry()))
                    .run(new WrappingIntoHandshake()
                            .type(server_hello)
                            .updateContext(Context.Element.server_hello))
                    .run(new WrappingIntoTLSPlaintexts()
                            .type(handshake)
                            .version(TLSv12))
                    .store()

                    .run(new NegotiatingServerDHSecret())

                    .run(new ComputingHandshakeTrafficKeys()
                            .server())

                    // send EncryptedExtensions
                    .run(new GeneratingEncryptedExtensions())
                    .run(new WrappingIntoHandshake()
                            .type(encrypted_extensions)
                            .updateContext(Context.Element.encrypted_extensions))
                    .run(new WrappingHandshakeDataIntoTLSCiphertext())
                    .store()

                    // send Certificate
                    .run(new GeneratingCertificate()
                            .certificate(config.serverCertificate()))
                    .run(new WrappingIntoHandshake()
                            .type(certificate)
                            .updateContext(Context.Element.server_certificate))
                    .run(new WrappingHandshakeDataIntoTLSCiphertext())
                    .store()

                    // send CertificateVerify
                    .run(new GeneratingCertificateVerify()
                            .server()
                            .key(config.serverKey()))
                    .run(new WrappingIntoHandshake()
                            .type(certificate_verify)
                            .updateContext(Context.Element.server_certificate_verify))
                    .run(new WrappingHandshakeDataIntoTLSCiphertext())
                    .store()

                    .run(new GeneratingFinished(Side.server))
                    .run(new WrappingIntoHandshake()
                            .type(finished)
                            .updateContext(Context.Element.server_finished))
                    .run(new WrappingHandshakeDataIntoTLSCiphertext())
                    .store()

                    .restore()
                    .send(new OutgoingData())

                    .receive(new IncomingData())

                    .run(new ProcessingHandshakeTLSCiphertext()
                            .expect(handshake))
                    .run(new ProcessingHandshake()
                            .expect(finished))
                    .run(new ProcessingFinished(Side.server))

                    .run(new ComputingApplicationTrafficKeys()
                            .server())

                    // receive application data
                    .receive(new IncomingData())
                    .run(new ProcessingApplicationDataTLSCiphertext()
                            .expect(application_data))
                    .run(new PrintingData())

                    // send application data
                    .run(new PreparingHttpResponse())
                    .run(new WrappingApplicationDataIntoTLSCiphertext())
                    .send(new OutgoingData());
        }
    }

    private static boolean checkContexts(
            Context clientContext, Context serverContext, Output output) {

        output.info("check client and server contexts");
        assertNotNull("client context should not be null", clientContext);
        assertNotNull("server context should not be null", serverContext);

        assertArrayEquals("contexts: dh_shared_secret are not equal",
                clientContext.dh_shared_secret,
                serverContext.dh_shared_secret);

        assertArrayEquals("contexts: early_secret are not equal",
                clientContext.early_secret,
                serverContext.early_secret);

        assertArrayEquals("contexts: binder_key are not equal",
                clientContext.binder_key,
                serverContext.binder_key);

        assertArrayEquals("contexts: client_early_traffic_secret are not equal",
                clientContext.client_early_traffic_secret,
                serverContext.client_early_traffic_secret);

        assertArrayEquals("contexts: early_exporter_master_secret are not equal",
                clientContext.early_exporter_master_secret,
                serverContext.early_exporter_master_secret);

        assertArrayEquals("contexts: handshake_secret_salt are not equal",
                clientContext.handshake_secret_salt,
                serverContext.handshake_secret_salt);

        assertArrayEquals("contexts: handshake_secret are not equal",
                clientContext.handshake_secret,
                serverContext.handshake_secret);

        assertArrayEquals("contexts: client_handshake_traffic_secret are not equal",
                clientContext.client_handshake_traffic_secret,
                serverContext.client_handshake_traffic_secret);

        assertArrayEquals("contexts: server_handshake_traffic_secret are not equal",
                clientContext.server_handshake_traffic_secret,
                serverContext.server_handshake_traffic_secret);

        assertArrayEquals("contexts: master_secret are not equal",
                clientContext.master_secret,
                serverContext.master_secret);

        assertArrayEquals("contexts: client_application_traffic_secret_0 are not equal",
                clientContext.client_application_traffic_secret_0,
                serverContext.client_application_traffic_secret_0);

        assertArrayEquals("contexts: server_application_traffic_secret_0 are not equal",
                clientContext.server_application_traffic_secret_0,
                serverContext.server_application_traffic_secret_0);

        assertArrayEquals("contexts: exporter_master_secret are not equal",
                clientContext.exporter_master_secret,
                serverContext.exporter_master_secret);

        assertArrayEquals("contexts: resumption_master_secret are not equal",
                clientContext.resumption_master_secret,
                serverContext.resumption_master_secret);

        assertArrayEquals("contexts: client_handshake_write_key are not equal",
                clientContext.client_handshake_write_key,
                serverContext.client_handshake_write_key);

        assertArrayEquals("contexts: client_handshake_write_iv are not equal",
                clientContext.client_handshake_write_iv,
                serverContext.client_handshake_write_iv);

        assertArrayEquals("contexts: server_handshake_write_key are not equal",
                clientContext.server_handshake_write_key,
                serverContext.server_handshake_write_key);

        assertArrayEquals("contexts: server_handshake_write_iv are not equal",
                clientContext.server_handshake_write_iv,
                serverContext.server_handshake_write_iv);

        assertArrayEquals("contexts: client_application_write_key are not equal",
                clientContext.client_application_write_key,
                serverContext.client_application_write_key);

        assertArrayEquals("contexts: client_application_write_iv are not equal",
                clientContext.client_application_write_iv,
                serverContext.client_application_write_iv);

        assertArrayEquals("contexts: server_application_write_key are not equal",
                clientContext.server_application_write_key,
                serverContext.server_application_write_key);

        assertArrayEquals("contexts: server_application_write_iv are not equal",
                clientContext.server_application_write_iv,
                serverContext.server_application_write_iv);

        return true;
    }

    private static class PreparingHttpResponse extends PreparingApplicationData {

        private static final byte[] HTML_PAGE =
                "<html>Like most of life's problems, this one can be solved with bending!<html>"
                        .getBytes();

        public PreparingHttpResponse() {
            super(HTML_PAGE);
        }

        @Override
        public String name() {
            return "generating HTTP response";
        }

    }
}
