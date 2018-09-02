package com.gypsyengineer.tlsbunny.tls13.client.common;

import com.gypsyengineer.tlsbunny.tls13.connection.AlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineException;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Side;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingMessages;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.security.NoSuchAlgorithmException;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.client_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.finished;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;

public class EmptyTLSPlaintext {

    private static final Config config = SystemPropertiesConfig.load();
    private static final StructFactory factory = StructFactory.getDefault();
    private static final ProtocolVersion protocolVersion = TLSv13_draft_26;
    private static final Output output = new Output();

    public static void main(String[] args) throws Exception {
        try (output) {
            /**
             * The TLS 1.3 spec says the following:
             *
             *    A change_cipher_spec record received before the first ClientHello message
             *    or after the peer's Finished message MUST be treated as an unexpected record type
             *
             *  https://tools.ietf.org/html/draft-ietf-tls-tls13-28#section-5
             */
            startWithEmptyTLSPlaintext(change_cipher_spec).run(new AlertCheck());

            /**
             * The TLS 1.3 spec says the following:
             *
             *      Implementations MUST NOT send None-length fragments of Handshake
             *      types, even if those fragments contain padding.
             *
             *  https://tools.ietf.org/html/draft-ietf-tls-tls13-28#section-5.1
             *
             *  Should it expect an alert them
             *  after sending an empty TLSPlaintext message of handshake type?
             */
            startWithEmptyTLSPlaintext(handshake).run(new NoAlertCheck());

            startWithEmptyTLSPlaintext(application_data).run(new AlertCheck());
            startWithEmptyTLSPlaintext(alert).run(new AlertCheck());
        }
    }

    private static Engine startWithEmptyTLSPlaintext(ContentType type)
            throws NegotiatorException, NoSuchAlgorithmException, EngineException {

        output.info("test: start handshake with an empty TLSPlaintext (%s)", type);

        return Engine.init()
                .target(config.host())
                .target(config.port())
                .set(factory)
                .set(output)

                .send(new GeneratingEmptyTLSPlaintext()
                        .type(type)
                        .version(TLSv12))

                // send ClientHello
                .run(new GeneratingClientHello()
                        .supportedVersions(protocolVersion)
                        .groups(secp256r1)
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
                .receive(new IncomingMessages(Side.client))

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

                // receive session tickets and application data
                .loop(context -> !context.receivedApplicationData())
                    .receive(() -> new IncomingMessages(Side.client))

                .connect();
    }

}
