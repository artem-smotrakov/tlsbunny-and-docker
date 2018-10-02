package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Side;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.connection.check.AlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.check.FailureCheck;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.server.SingleThreadServer;
import com.gypsyengineer.tlsbunny.tls13.struct.AlertDescription;
import com.gypsyengineer.tlsbunny.tls13.struct.AlertLevel;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.gypsyengineer.tlsbunny.tls13.client.FuzzyClient.*;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This is just a smoke test for the fuzzy https client
 * because the server sends an alert immediately,
 * so that the fuzzer may not be able to fuzz a message
 * if it goes later in the handshake process (for example, Finished message).
 */
public class FuzzyClientTest {

    private static final int start = 10;
    private static final int end = 15;
    private static final int parts = 1;

    private Config clientConfig = SystemPropertiesConfig.load();

    private static final Check[] checks = {
            new AlertCheck(),
            new FailureCheck()
    };

    @Test
    public void tlsPlaintext() throws Exception {
        test(minimized(tlsPlaintextConfigs(clientConfig)));
    }

    @Test
    public void handshake() throws Exception {
        test(minimized(handshakeConfigs(clientConfig)));
    }

    @Test
    public void clientHello() throws Exception {
        test(minimized(clientHelloConfigs()));
    }

    @Test
    public void ccs() throws Exception {
        test(minimized(ccsConfigs(clientConfig)));
    }

    @Test
    public void finished() throws Exception {
        test(minimized(finishedConfigs(clientConfig)));
    }

    @Test
    public void cipherSuites() throws Exception {
        test(minimized(cipherSuitesConfigs(clientConfig)));
    }

    @Test
    public void extensionVector() throws Exception {
        test(minimized(extensionVectorConfigs(clientConfig)));
    }

    @Test
    public void legacySessionId() throws Exception {
        test(minimized(legacySessionIdConfigs(clientConfig)));
    }

    @Test
    public void legacyCompressionMethods() throws Exception {
        test(minimized(legacyCompressionMethodsConfigs(clientConfig)));
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
                .maxConnections(end - start + 2);

        FuzzyHttpsClient fuzzyClient = new FuzzyHttpsClient();

        TestAnalyzer analyzer = new TestAnalyzer();
        analyzer.set(clientOutput);

        try (fuzzyClient; server; clientOutput; serverOutput) {
            server.start();

            for (FuzzerConfig fuzzerConfig : configs) {
                fuzzerConfig.port(server.port());
            }

            fuzzyClient.set(configs)
                    .set(clientConfig)
                    .set(clientOutput)
                    .set(analyzer)
                    .connect();
        }

        analyzer.run();
        assertEquals(end - start + 1, analyzer.engines().length);
        for (Engine engine : analyzer.engines()) {
            assertTrue(engine.context().hasAlert());
            assertEquals(
                    AlertLevel.fatal,
                    engine.context().getAlert().getLevel());
            assertEquals(
                    AlertDescription.close_notify,
                    engine.context().getAlert().getDescription());
        }
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
                            .description(AlertDescription.close_notify))
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

    private static class TestAnalyzer implements Analyzer {

        private Output output;
        private final List<Engine> engines = new ArrayList<>();

        @Override
        public Analyzer set(Output output) {
            this.output = output;
            return this;
        }

        @Override
        public Analyzer add(Engine... engines) {
            this.engines.addAll(List.of(engines));
            return this;
        }

        @Override
        public Analyzer run() {
            output.info("run analyzer");
            return this;
        }

        @Override
        public Engine[] engines() {
            return engines.toArray(new Engine[engines.size()]);
        }
    }

    private static FuzzerConfig[] minimized(FuzzerConfig[] configs) {
        for (FuzzerConfig config : configs) {
            config.startTest(start);
            config.endTest(end);
            config.parts(parts);
            config.set(checks);
        }

        return configs;
    }
}
