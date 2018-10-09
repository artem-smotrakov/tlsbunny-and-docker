package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls13.client.HttpsClientAuth;
import com.gypsyengineer.tlsbunny.tls13.connection.BaseEngineFactory;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Side;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.server.OneConnectionReceived;
import com.gypsyengineer.tlsbunny.tls13.server.SingleThreadServer;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import java.util.List;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.HandshakeDeepFuzzer.handshakeDeepFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.application_data;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.certificate;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.certificate_verify;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv13;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.rsa_pkcs1_sha256;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HandshakeDeepFuzzerTest {

    @Test
    public void recording() {
        HandshakeDeepFuzzer fuzzer = handshakeDeepFuzzer();
        assertTrue(fuzzer.recorded().length == 0);

        // check that recording is not enabled by default
        assertNotNull(createClientHello(fuzzer));
        assertTrue(fuzzer.recorded().length == 0);

        // enable recording
        fuzzer.recording();
        assertTrue(fuzzer.recorded().length == 0);
        assertNotNull(createClientHello(fuzzer));
        assertTrue(fuzzer.recorded().length == 1);
        assertNotNull(createClientHello(fuzzer));
        assertTrue(fuzzer.recorded().length == 2);
        assertNotNull(createFinished(fuzzer));
        assertTrue(fuzzer.recorded().length == 3);
        assertArrayEquals(
                fuzzer.recorded(),
                new HandshakeType[] { client_hello, client_hello, finished });

        // disable recording
        fuzzer.fuzzing();
        assertTrue(fuzzer.recorded().length == 3);
        assertArrayEquals(
                fuzzer.recorded(),
                new HandshakeType[] { client_hello, client_hello, finished});
        assertNotNull(createClientHello(fuzzer));
        assertTrue(fuzzer.recorded().length == 3);
        assertArrayEquals(
                fuzzer.recorded(),
                new HandshakeType[] { client_hello, client_hello, finished });

        // enable recording again
        fuzzer.recording();
        assertTrue(fuzzer.recorded().length == 0);
        assertNotNull(createClientHello(fuzzer));
        assertTrue(fuzzer.recorded().length == 1);
        assertNotNull(createFinished(fuzzer));
        assertTrue(fuzzer.recorded().length == 2);
        assertNotNull(createFinished(fuzzer));
        assertTrue(fuzzer.recorded().length == 3);
        assertArrayEquals(
                fuzzer.recorded(),
                new HandshakeType[] { client_hello, finished, finished });
    }

    private static ClientHello createClientHello(StructFactory factory) {
        return factory.createClientHello(
                TLSv13,
                new Random(),
                new byte[32],
                List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                List.of(CompressionMethod.None),
                List.of());
    }

    private static Finished createFinished(StructFactory factory) {
        return factory.createFinished(new byte[32]);
    }

    @Test
    public void handshake() throws Exception {
        Output serverOutput = new Output("server");
        Output clientOutput = new Output("client");

        Config serverConfig = SystemPropertiesConfig.load();
        SingleThreadServer server = new SingleThreadServer()
                .set(new EngineFactoryImpl()
                        .set(serverConfig)
                        .set(serverOutput))
                .set(serverConfig)
                .set(serverOutput)
                .stopWhen(new OneConnectionReceived());

        HttpsClientAuth client = new HttpsClientAuth();

        HandshakeDeepFuzzer fuzzer = handshakeDeepFuzzer();
        fuzzer.recording();

        try (server; clientOutput; serverOutput) {
            server.start();

            Config clientConfig = SystemPropertiesConfig.load()
                    .port(server.port());
            client.set(fuzzer).set(clientConfig).set(clientOutput);

            try (client) {
                client.connect().engine().apply(new NoAlertAnalyzer());
            }
        }

        assertArrayEquals(
                fuzzer.recorded(),
                new HandshakeType[] { client_hello, certificate, certificate_verify, finished });
    }

    private static class EngineFactoryImpl extends BaseEngineFactory {

        private Config config;

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

                    // send CertificateRequest
                    .run(new GeneratingCertificateRequest()
                            .signatures(rsa_pkcs1_sha256))
                    .run(new WrappingIntoHandshake()
                            .type(certificate_request)
                            .updateContext(Context.Element.server_certificate_request))
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

                    .run(new ComputingApplicationTrafficKeys()
                            .server())

                    .receive(new IncomingData())
                    .run(new ProcessingHandshakeTLSCiphertext()
                            .expect(handshake))
                    .run(new ProcessingHandshake()
                            .expect(certificate)
                            .updateContext(Context.Element.client_certificate))
                    .run(new ProcessingCertificate())

                    .receive(new IncomingData())
                    .run(new ProcessingHandshakeTLSCiphertext()
                            .expect(handshake))
                    .run(new ProcessingHandshake()
                            .expect(certificate_verify)
                            .updateContext(Context.Element.client_certificate_verify))
                    .run(new ProcessingCertificateVerify())

                    .receive(new IncomingData())
                    .run(new ProcessingHandshakeTLSCiphertext()
                            .expect(handshake))
                    .run(new ProcessingHandshake()
                            .expect(finished))
                    .run(new ProcessingFinished(Side.server))

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
}
