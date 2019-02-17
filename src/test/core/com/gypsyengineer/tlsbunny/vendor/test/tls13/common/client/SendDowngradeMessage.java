package com.gypsyengineer.tlsbunny.vendor.test.tls13.common.client;

import com.gypsyengineer.tlsbunny.tls13.connection.check.AlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.BaseEngineFactory;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Side;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingMessages;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.tls13.server.SingleThreadServer;
import com.gypsyengineer.tlsbunny.tls13.server.OneConnectionReceived;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.output.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.certificate_verify;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.finished;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;

// TODO: it should be a test
public class SendDowngradeMessage {

    // TODO: how can we avoid duplicating these lines?
    private static final String serverCertificatePath = "certs/server_cert.der";
    private static final String serverKeyPath = "certs/server_key.pkcs8";

    public static void main(String[] args) throws Exception {
        go(TLSv12);
    }

    public static void go(ProtocolVersion version) throws Exception {
        Config config = SystemPropertiesConfig.load();
        config.serverCertificate(serverCertificatePath);
        config.serverKey(serverKeyPath);

        try (Output output = Output.standard("server");
             Server server = server(output, config, version)) {

            server.start().join();

            if (server.failed()) {
                throw new Exception("server failed! " +
                        "looks like one of the client didn't notice our downgrade message, " +
                        "and didn't send an alert");
            }
        }
    }

    // TODO: max connections should be configurable
    public static Server server(Output output, Config config, ProtocolVersion version)
            throws IOException {

        return new SingleThreadServer()
                .set(new EngineFactoryImpl()
                        .downgradeVersion(version)
                        .set(config)
                        .set(output))
                .set(config)
                .set(output)
                .set(new AlertCheck())
                .stopWhen(new OneConnectionReceived());
    }

    public static class EngineFactoryImpl extends BaseEngineFactory {

        // TODO: add synchronization
        private ProtocolVersion downgradeVersion = null;

        public EngineFactoryImpl downgradeVersion(ProtocolVersion version) {
            downgradeVersion = version;
            return this;
        }

        @Override
        protected Engine createImpl() throws Exception {
            return Engine.init()
                    .set(structFactory)
                    .set(output)

                    // receive ClientHello
                    .receive(new IncomingMessages(Side.server))

                    // send ServerHello with a downgrade message
                    .run(new GeneratingServerHello()
                            .supportedVersion(downgradeVersion)
                            .downgradeProtection(downgradeVersion)
                            .group(secp256r1)
                            .signatureScheme(ecdsa_secp256r1_sha256)
                            .keyShareEntry(context -> context.negotiator().createKeyShareEntry()))
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

                    .receive(new IncomingMessages(Side.server));
        }
    }
}
