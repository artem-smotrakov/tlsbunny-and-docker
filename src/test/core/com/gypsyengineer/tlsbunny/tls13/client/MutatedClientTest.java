package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.TestUtils;
import com.gypsyengineer.tlsbunny.tls13.client.fuzzer.MutatedClient;
import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Side;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.server.SingleThreadServer;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.gypsyengineer.tlsbunny.tls13.client.fuzzer.MutatedClient.*;
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
import static org.junit.Assert.assertFalse;

public class MutatedClientTest {

    private static final int start = 2;
    private static final int end = 3;
    private static final int parts = 1;

    // number of connections during fuzzing (we don't forget a smoke test)
    private static final int n = end - start + 2;

    private Config clientConfig = SystemPropertiesConfig.load();

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
        test(minimized(clientHelloConfigs(clientConfig)));
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
        for (FuzzerConfig config : configs) {
            test(config);
        }
    }

    public void test(FuzzerConfig fuzzerConfig) throws Exception {
        Output serverOutput = new Output("server");
        Output clientOutput = new Output("client");

        Config serverConfig = SystemPropertiesConfig.load();
        SingleThreadServer server = new SingleThreadServer()
                .set(new EngineFactoryImpl()
                        .set(serverConfig)
                        .set(serverOutput))
                .set(serverConfig)
                .set(serverOutput)
                .maxConnections(n);

        MutatedClient fuzzyClient = new MutatedClient(
                new HttpsClient(), clientOutput, fuzzerConfig);

        TestAnalyzer analyzer = new TestAnalyzer();
        analyzer.set(clientOutput);

        try (fuzzyClient; server; clientOutput; serverOutput) {
            server.start();

            fuzzerConfig.port(server.port());

            fuzzyClient
                    .set(fuzzerConfig)
                    .set(clientOutput)
                    .set(analyzer)
                    .connect();
        }

        analyzer.run();
        assertEquals(n, analyzer.engines().length);
        for (Engine engine : analyzer.engines()) {
            assertFalse(engine.context().hasAlert());
        }
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
            return engines.toArray(new Engine[0]);
        }
    }

    private static FuzzerConfig[] minimized(FuzzerConfig[] configs) {
        FuzzerConfig config = configs[0];
        config.startTest(start);
        config.endTest(end);
        config.parts(parts);

        if (config.factory() instanceof MutatedStructFactory) {
            MutatedStructFactory factory = (MutatedStructFactory) config.factory();
            factory.fuzzer(new TestUtils.FakeFlipFuzzer());
        }

        if (config.factory() instanceof LegacySessionIdFuzzer) {
            LegacySessionIdFuzzer factory = (LegacySessionIdFuzzer) config.factory();
            factory.fuzzer(new TestUtils.FakeVectorFuzzer());
        }

        if (config.factory() instanceof LegacyCompressionMethodsFuzzer) {
            LegacyCompressionMethodsFuzzer factory = (LegacyCompressionMethodsFuzzer) config.factory();
            factory.fuzzer(new TestUtils.FakeCompressionMethodFuzzer());
        }

        if (config.factory() instanceof CipherSuitesFuzzer) {
            CipherSuitesFuzzer factory = (CipherSuitesFuzzer) config.factory();
            factory.fuzzer(new TestUtils.FakeCipherSuitesFuzzer());
        }

        if (config.factory() instanceof ExtensionVectorFuzzer) {
            ExtensionVectorFuzzer factory = (ExtensionVectorFuzzer) config.factory();
            factory.fuzzer(new TestUtils.FakeExtensionVectorFuzzer());
        }

        return new FuzzerConfig[] { config };
    }
}
