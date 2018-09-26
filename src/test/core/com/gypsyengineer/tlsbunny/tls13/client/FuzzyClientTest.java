package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.BaseEngineFactory;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Side;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.server.SingleThreadServer;
import com.gypsyengineer.tlsbunny.tls13.struct.AlertDescription;
import com.gypsyengineer.tlsbunny.tls13.struct.AlertLevel;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.client.FuzzyClient.ccsConfigs;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.alert;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.application_data;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.certificate_verify;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.finished;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv13;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;

public class FuzzyClientTest {

    private static final int start = 10;
    private static final int end = 15;
    private static final int parts = 1;
    private static final int number_of_connections = end - start + 1;

    static {
        System.setProperty("tlsbunny.threads", "1");
    }

    @Test
    public void ccs() throws Exception {
        test(minimized(ccsConfigs()));
    }

    public void test(FuzzerConfig[] configs) throws Exception {
        Output serverOutput = new Output("server");
        Output clientOutput = new Output("client");

        Config serverConfig = SystemPropertiesConfig.load();
        SingleThreadServer server = new SingleThreadServer()
                .set(new EngineFactoryImpl()
                        .set(serverConfig)
                        .set(serverOutput))
                .set(serverConfig)
                .set(serverOutput)
                .maxConnections(number_of_connections);

        FuzzyHttpsClient fuzzyClient = new FuzzyHttpsClient();

        try (fuzzyClient; server; clientOutput; serverOutput) {
            server.start();
            Config clientConfig = SystemPropertiesConfig.load().port(server.port());

            fuzzyClient.set(configs)
                    .set(clientConfig)
                    .set(clientOutput)
                    .connect();
        }

        // TODO: add asserts
    }

    private static class EngineFactoryImpl extends BaseEngineFactory {

        private boolean generateAlert = false;

        public EngineFactoryImpl set(Config config) {
            this.config = config;
            return this;
        }

        @Override
        protected Engine createImpl() throws Exception {
            if (generateAlert) {
                return sendAlert();
            }

            return fullHandshake();
        }

        private Engine sendAlert() throws Exception {
            return Engine.init()
                    .set(structFactory)
                    .set(output)

                    .receive(new IncomingData())
                    .run(new PrintingData())

                    // send an alert
                    .run(new GeneratingAlert()
                            .level(AlertLevel.fatal)
                            .description(AlertDescription.unexpected_message))
                    .run(new WrappingIntoTLSPlaintexts()
                            .version(TLSv12)
                            .type(alert))
                    .send(new OutgoingData());
        }

        private Engine fullHandshake() throws Exception {
            // we do a full handshake only once to pass a smoke test
            generateAlert = true;

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

                    .receive(new IncomingChangeCipherSpec())

                    // send ServerHello
                    .run(new GeneratingServerHello()
                            .supportedVersion(TLSv13)
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

                    .run(new OutgoingChangeCipherSpec())
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

    private static FuzzerConfig[] minimized(FuzzerConfig[] configs) {
        for (FuzzerConfig config : configs) {
            config.startTest(start);
            config.endTest(end);
            config.parts(parts);
        }

        return configs;
    }
}
