package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import java.util.List;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.application_data;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv13_draft_26;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;

public class TooManyGroupsInClientHello extends AbstractClient {

    public static void main(String[] args) throws Exception {
        try (Output output = new Output()) {
            new TooManyGroupsInClientHello()
                    .set(output)
                    .set(SystemPropertiesConfig.load())
                    .set(StructFactory.getDefault())
                    .connect();
        }
    }

    @Override
    protected Engine createEngine() throws Exception {
        int n = 65535 / 2 - 48;
        NamedGroup[] tooManyGroups = new NamedGroup[n];
        for (int i = 0; i < n; i++) {
            tooManyGroups[i] = secp256r1;
        }

        return Engine.init()
                .target(config.host())
                .target(config.port())
                .set(factory)
                .set(output)

                // send ClientHello
                .run(new GeneratingClientHello()
                        .supportedVersions(TLSv13_draft_26)
                        .groups(tooManyGroups)
                        .signatureSchemes(ecdsa_secp256r1_sha256)
                        .keyShareEntries(context -> context.negotiator.createKeyShareEntry()))
                .run(new WrappingIntoHandshake()
                        .type(client_hello)
                        .updateContext(Context.Element.first_client_hello))
                .run(new WrappingIntoTLSPlaintexts()
                        .type(handshake)
                        .version(TLSv12))
                .send(new OutgoingData())

                // receive a ServerHello, EncryptedExtensions, Certificate,
                // CertificateVerify and Finished messages
                .receive(new IncomingData())

                // process ServerHello
                .run(new ProcessingTLSPlaintext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(server_hello)
                        .updateContext(Context.Element.server_hello))
                .run(new ProcessingServerHello())
                .run(new NegotiatingClientDHSecret())
                .run(new ComputingHandshakeTrafficKeys()
                        .client())

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
                .run(new ProcessingHandshakeTLSCiphertext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(finished)
                        .updateContext(Context.Element.server_finished))
                .run(new ProcessingFinished())
                .run(new ComputingApplicationTrafficKeys()
                        .client())

                // send Finished
                .run(new GeneratingFinished())
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
                .receive(new IncomingData())
                .run(new ProcessingApplicationDataTLSCiphertext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(new_session_ticket))
                .run(new ProcessingNewSessionTicket())

                // receive second NewSessionTicket
                .receive(new IncomingData())
                .run(new ProcessingApplicationDataTLSCiphertext()
                        .expect(handshake))
                .run(new ProcessingHandshake().expect(new_session_ticket))
                .run(new ProcessingNewSessionTicket())

                // receive application data
                .receive(new IncomingData())
                .run(new ProcessingApplicationDataTLSCiphertext()
                        .expect(application_data))
                .run(new PrintingData());
    }

    @Override
    protected List<Check> createChecks() {
        return List.of(new NoAlertCheck());
    }

}
