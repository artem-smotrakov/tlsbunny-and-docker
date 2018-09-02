package com.gypsyengineer.tlsbunny.tls13.client.common.downgrade;

import com.gypsyengineer.tlsbunny.tls13.connection.AbstractCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.client.common.AbstractClient;
import com.gypsyengineer.tlsbunny.utils.Output;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.alert;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.client_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.server_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;

public class NoSupportedVersions extends AbstractClient {

    public static void main(String[] args) throws Exception {
        try (Output output = new Output()) {
            run(output, SystemPropertiesConfig.load());
        }
    }

    public static NoSupportedVersions run(Output output, Config config) throws Exception {
        NoSupportedVersions client = (NoSupportedVersions) new NoSupportedVersions()
                .set(config)
                .set(StructFactory.getDefault())
                .set(output);

        client.connect().run(new DowngradeMessageCheck().ifTLSv12());
        return client;
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
                .run(new WrappingIntoTLSPlaintexts()
                        .type(alert)
                        .version(TLSv12))
                .send(new OutgoingData());
    }

    public static class DowngradeMessageCheck extends AbstractCheck {

        private static final byte[] downgrade_tls12_message = new byte[] {
                0x44, 0x4F, 0x57, 0x4E, 0x47, 0x52, 0x44, 0x01
        };
        private static final byte[] downgrade_tls11_and_below_message = new byte[] {
                0x44, 0x4F, 0x57, 0x4E, 0x47, 0x52, 0x44, 0x00
        };

        private byte[] downgrade_message = downgrade_tls12_message;

        public DowngradeMessageCheck ifTLSv12() {
            downgrade_message = downgrade_tls12_message;
            return this;
        }

        public DowngradeMessageCheck ifBelowTLSv12() {
            downgrade_message = downgrade_tls11_and_below_message;
            return this;
        }

        @Override
        public Check run() {
            if (context.getServerHello() == null) {
                return this;
            }

            ServerHello hello = StructFactory.getDefault().parser().parseServerHello(
                    context.getServerHello().getBody());

            byte[] bytes = hello.getRandom().getBytes();
            int i = bytes.length - downgrade_message.length;
            int j = 0;
            while (j < downgrade_message.length) {
                if (bytes[i++] != downgrade_message[j++]) {
                    return this;
                }
            }

            failed = false;

            return this;
        }

        @Override
        public String name() {
            return "downgrade message received in ServerHello.random";
        }
    }

}
