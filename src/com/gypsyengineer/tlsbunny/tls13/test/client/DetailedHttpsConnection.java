package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.application_data;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv13;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;

public class DetailedHttpsConnection {

    public static void main(String[] args) throws Exception {
        CommonConfig config = new CommonConfig();

        // TODO: fix it
        // TODO: WrappingIntoHandshake and ProcessingHandshake
        //       should have methods for updating context
        Engine.init()
                .target(config.host())
                .target(config.port())

                // send ClientHello
                .run(new GeneratingClientHello()
                        .supportedVersion(TLSv13)
                        .group(secp256r1)
                        .signatureScheme(ecdsa_secp256r1_sha256)
                        .keyShareEntry(context -> context.negotiator.createKeyShareEntry()))
                .run(new WrappingIntoHandshake()
                        .type(client_hello)
                        .run((context, message) -> context.setFirstClientHello(message)))
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
                        .run((context, message) -> context.setServerHello(message)))
                .run(new ProcessingServerHello())
                .run(new NegotiatingDHSecret())
                .run(new ComputingKeysAfterServerHello())

                .allow(new IncomingChangeCipherSpec())

                // process EncryptedExtensions
                .run(new ProcessingHandshakeTLSCiphertext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(encrypted_extensions)
                        .run((context, message) -> context.setEncryptedExtensions(message)))
                .run(new ProcessingEncryptedExtensions())

                // process Certificate
                .run(new ProcessingHandshakeTLSCiphertext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(certificate)
                        .run((context, message) -> context.setServerCertificate(message)))
                .run(new ProcessingCertificate())

                // process CertificateVerify
                .run(new ProcessingHandshakeTLSCiphertext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(certificate_verify)
                        .run((context, message) -> context.setServerCertificateVerify(message)))
                .run(new ProcessingCertificateVerify())

                // process Finished
                .run(new ProcessingHandshakeTLSCiphertext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(finished)
                        .run((context, message) -> context.setServerFinished(message)))
                .run(new ProcessingFinished())
                .run(new ComputingKeysAfterServerFinished())

                // send Finished
                .run(new GeneratingFinished())
                .run(new ComputingKeysAfterClientFinished())
                .run(new WrappingIntoHandshake()
                        .type(finished)
                        .run((context, message) -> context.setClientFinished(message)))
                .run(new WrappingIntoTLSCiphertext(WrappingIntoTLSCiphertext.Phase.handshake)
                        .type(handshake))
                .send(new OutgoingData())

                // receive NewSessionTicket
                .require(new IncomingData())
                .run(new ProcessingApplicationDataTLSCiphertext())
                .run(new ProcessingHandshake()
                        .expect(new_session_ticket))
                .run(new ProcessingNewSessionTicket())

                // send application data
                .run(new PreparingHttpGetRequest())
                .run(new WrappingIntoTLSCiphertext(WrappingIntoTLSCiphertext.Phase.application_data)
                        .type(application_data))
                .send(new OutgoingData())

                // receive application data
                .require(new IncomingData())
                .run(new ProcessingApplicationDataTLSCiphertext())
                .run(new PrintingData())

                .connect()
                .run(new NoAlertCheck());
    }

}
