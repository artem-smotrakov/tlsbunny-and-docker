package com.gypsyengineer.tlsbunny.tls13.server;

import com.gypsyengineer.tlsbunny.tls13.connection.BaseEngineFactory;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Side;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.application_data;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.change_cipher_spec;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.certificate_verify;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.finished;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv13;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class HttpsServer implements Server {

    private final EngineFactoryImpl engineFactory;
    private final SingleThreadServer server;

    public static HttpsServer httpsServer()
            throws IOException, NegotiatorException {

        return httpsServer(SingleThreadServer.free_port);
    }

    public static HttpsServer httpsServer(int port)
            throws IOException, NegotiatorException {

        EngineFactoryImpl factory = new EngineFactoryImpl()
                .set(secp256r1)
                .set(StructFactory.getDefault());

        return new HttpsServer(new SingleThreadServer(port), factory);
    }

    private HttpsServer(SingleThreadServer server, EngineFactoryImpl engineFactory) {
        this.engineFactory = engineFactory;
        this.server = server;
        server.set(engineFactory);
    }

    @Override
    public HttpsServer set(Config config) {
        engineFactory.set(config);
        server.set(config);
        return this;
    }

    @Override
    public HttpsServer set(EngineFactory engineFactory) {
        throw whatTheHell("you can't set an engine engineFactory for me!");
    }

    @Override
    public HttpsServer set(Check check) {
        server.set(check);
        return this;
    }

    @Override
    public HttpsServer stopWhen(StopCondition condition) {
        server.stopWhen(condition);
        return this;
    }

    @Override
    public Thread start() {
        return server.start();
    }

    @Override
    public HttpsServer stop() {
        server.stop();
        return this;
    }

    @Override
    public boolean running() {
        return server.running();
    }

    @Override
    public int port() {
        return server.port();
    }

    @Override
    public Engine recentEngine() {
        return server.recentEngine();
    }

    @Override
    public Engine[] engines() {
        return server.engines();
    }

    @Override
    public boolean failed() {
        return server.failed();
    }

    @Override
    public HttpsServer set(Output output) {
        engineFactory.set(output);
        server.set(output);
        return this;
    }

    @Override
    public Output output() {
        return server.output();
    }

    @Override
    public void close() throws Exception {
        server.close();
    }

    @Override
    public void run() {
        server.run();
    }

    public HttpsServer maxConnections(int n) {
        server.maxConnections(n);
        return this;
    }

    public HttpsServer set(NamedGroup group) throws NegotiatorException {
        engineFactory.set(group);
        return this;
    }

    public HttpsServer set(StructFactory structFactory) {
        engineFactory.set(structFactory);
        return this;
    }

    private static class EngineFactoryImpl extends BaseEngineFactory {

        private Negotiator negotiator;

        public EngineFactoryImpl set(NamedGroup group) throws NegotiatorException {
            negotiator = Negotiator.create(group, structFactory());
            return this;
        }

        @Override
        public EngineFactoryImpl set(StructFactory structFactory) {
            super.set(structFactory);
            negotiator.set(structFactory);
            return this;
        }

        @Override
        protected Engine createImpl() throws Exception {
            return Engine.init()
                    .set(structFactory)
                    .set(output)
                    .set(negotiator)
                    .set(negotiator.group())

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

                    .run(new ProcessingTLSPlaintext().expect(change_cipher_spec))
                    .run(new ProcessingChangeCipherSpec())

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
}
