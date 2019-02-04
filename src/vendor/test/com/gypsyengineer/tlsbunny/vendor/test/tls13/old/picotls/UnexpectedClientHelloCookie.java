package com.gypsyengineer.tlsbunny.vendor.test.tls13.old.picotls;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.check.FailureCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.output.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.client.SingleConnectionClient;

import java.util.List;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.application_data;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv13_draft_26;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;
import static com.gypsyengineer.tlsbunny.utils.Utils.zeroes;

public class UnexpectedClientHelloCookie extends SingleConnectionClient {

    public static final int ZERO_COOKIE_LENGTH = 32;

    public static void main(String[] args) throws Exception {
        try (Output output = Output.console()) {
            new UnexpectedClientHelloCookie()
                    .set(SystemPropertiesConfig.load())
                    .set(StructFactory.getDefault())
                    .set(output)
                    .connect();
        }
    }

    public UnexpectedClientHelloCookie() {
        checks = List.of(new FailureCheck());
    }

    @Override
    protected Engine createEngine() throws Exception {
        return Engine.init()
                .target(config.host())
                .target(config.port())
                .set(factory)
                .set(output)

                // send ClientHello
                .run(new GeneratingClientHello()
                        .supportedVersions(TLSv13_draft_26)
                        .groups(secp256r1)
                        .signatureSchemes(ecdsa_secp256r1_sha256)
                        .keyShareEntries(context -> context.negotiator().createKeyShareEntry())
                        .cookie(zeroes(ZERO_COOKIE_LENGTH)))
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

                // store application data which we can't decrypt yet
                .run(new PreservingEncryptedApplicationData())

                // send Finished
                .run(new GeneratingFinished())
                .run(new WrappingIntoHandshake()
                        .type(finished)
                        .updateContext(Context.Element.client_finished))
                .run(new WrappingHandshakeDataIntoTLSCiphertext())
                .send(new OutgoingData())

                // restore the stored application data
                .run(new RestoringEncryptedApplicationData())

                // decrypt the application data
                .run(new ProcessingApplicationDataTLSCiphertext()
                        .expect(application_data))
                .run(new PrintingData());
    }

}
