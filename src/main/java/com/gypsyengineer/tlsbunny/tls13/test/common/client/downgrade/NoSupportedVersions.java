package com.gypsyengineer.tlsbunny.tls13.test.common.client.downgrade;

import com.gypsyengineer.tlsbunny.tls13.connection.AbstractCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.AbstractClient;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.client_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.server_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;

public class NoSupportedVersions extends AbstractClient {

    public static void main(String[] args) throws Exception {
        try (Output output = new Output()) {
            new NoSupportedVersions()
                    .set(SystemPropertiesConfig.load())
                    .set(StructFactory.getDefault())
                    .set(output)
                    .connect()
                    .run(new NoAlertCheck());
        }
    }

    @Override
    protected Engine createEngine() throws Exception {
        return Engine.init()
                .target(config.host())
                .target(config.port())
                .set(factory)
                .set(output)

                // send ClientHello without SupportedVersions extensions
                // instead, just set legacy_protocol to TLSv12
                .run(new GeneratingClientHello()
                        .legacyVersion(TLSv12)
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

                // receive a ServerHello
                .receive(new IncomingData())

                // process ServerHello
                .run(new ProcessingTLSPlaintext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(server_hello)
                        .updateContext(Context.Element.server_hello))
                .run(new ProcessingServerHello())

                // send an alert
                .run(new GeneratingAlert())
                .run(new WrappingIntoTLSPlaintexts())
                .send(new OutgoingData())

                .run(new DowngradeMessageCheck());
    }

    private static class DowngradeMessageCheck extends AbstractCheck {

        @Override
        public Check run() {
            // TODO
            return this;
        }

        @Override
        public String name() {
            return "downgrade message received in ServerHello.random";
        }
    }

}
