package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.test.Config;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.Client;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;

public class HttpsClient implements Client {

    public static void main(String[] args) throws Exception {
        new HttpsClient()
                .connect(SystemPropertiesConfig.load(), StructFactory.getDefault())
                .run(new NoAlertCheck());
    }

    @Override
    public Engine connect(Config config, StructFactory factory) throws Exception {
        return Engine.init()
                .target(config.host())
                .target(config.port())
                .set(factory)

                // send ClientHello
                .run(new GeneratingClientHello()
                        .supportedVersion(TLSv13_draft_26)
                        .group(secp256r1)
                        .signatureScheme(ecdsa_secp256r1_sha256)
                        .keyShareEntry(context -> context.negotiator.createKeyShareEntry()))
                .run(new WrappingIntoHandshake()
                        .type(client_hello)
                        .updateContext(Context.Element.first_client_hello))
                .run(new WrappingIntoTLSPlaintexts()
                        .type(handshake)
                        .version(TLSv12))
                .send(new OutgoingData())

                // receive a ServerHello, EncryptedExtensions, Certificate,
                // CertificateVerify and Finished messages
                .require(new IncomingData())

                // process ServerHello
                .run(new ProcessingTLSPlaintext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(server_hello)
                        .updateContext(Context.Element.server_hello))
                .run(new ProcessingServerHello())
                .run(new NegotiatingDHSecret())
                .run(new ComputingKeysAfterServerHello())

                .allow(new IncomingChangeCipherSpec())

                // process EncryptedExtensions
                .run(new ProcessingHandshakeTLSCiphertext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(encrypted_extensions)
                        .updateContext(Context.Element.encrypted_extensions))
                .run(new ProcessingEncryptedExtensions())

                // process Certificate
                .run(new ProcessingHandshakeTLSCiphertext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(certificate)
                        .updateContext(Context.Element.server_certificate))
                .run(new ProcessingCertificate())

                // process CertificateVerify
                .run(new ProcessingHandshakeTLSCiphertext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(certificate_verify)
                        .updateContext(Context.Element.server_certificate_verify))
                .run(new ProcessingCertificateVerify())

                // process Finished
                .run(new ProcessingHandshakeTLSCiphertext())
                .run(new ProcessingHandshake()
                        .expect(finished)
                        .updateContext(Context.Element.server_finished))
                .run(new ProcessingFinished())
                .run(new ComputingKeysAfterServerFinished())

                // send Finished
                .run(new GeneratingFinished())
                .run(new ComputingKeysAfterClientFinished())
                .run(new WrappingIntoHandshake()
                        .type(finished)
                        .updateContext(Context.Element.client_finished))
                .run(new WrappingHandshakeDataIntoTLSCiphertext())
                .send(new OutgoingData())

                // send application data
                .run(new PreparingHttpGetRequest())
                .run(new WrappingApplicationDataIntoTLSCiphertext())
                .send(new OutgoingData())

                // receive first NewSessionTicket
                .require(new IncomingData())
                .run(new ProcessingApplicationDataTLSCiphertext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(new_session_ticket))
                .run(new ProcessingNewSessionTicket())

                // receive second NewSessionTicket
                .require(new IncomingData())
                .run(new ProcessingApplicationDataTLSCiphertext()
                        .expect(handshake))
                .run(new ProcessingHandshake().expect(new_session_ticket))
                .run(new ProcessingNewSessionTicket())

                // receive application data
                .require(new IncomingData())
                .run(new ProcessingApplicationDataTLSCiphertext())
                .run(new PrintingData())

                .connect();
    }

}
