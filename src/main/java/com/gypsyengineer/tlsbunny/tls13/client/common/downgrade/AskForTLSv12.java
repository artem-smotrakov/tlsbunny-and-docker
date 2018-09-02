package com.gypsyengineer.tlsbunny.tls13.client.common.downgrade;

import com.gypsyengineer.tlsbunny.tls13.client.common.AbstractClient;
import com.gypsyengineer.tlsbunny.tls13.connection.AbstractCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.action.DowngradeMessageCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.alert;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.client_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.server_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;

public class AskForTLSv12 extends AbstractClient {

    public static void main(String[] args) throws Exception {
        try (Output output = new Output()) {
            run(output, SystemPropertiesConfig.load());
        }
    }

    public static AskForTLSv12 run(Output output, Config config) throws Exception {
        AskForTLSv12 client = (AskForTLSv12) new AskForTLSv12()
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

                // send ClientHello with a SupportedVersions extension
                // which contains TLSv12
                .run(new GeneratingClientHello()
                        .legacyVersion(TLSv12)
                        .group(secp256r1)
                        .supportedVersion(TLSv12)
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

}
