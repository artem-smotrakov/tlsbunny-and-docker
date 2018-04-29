package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.test.CommonConfig;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.client_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.server_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv13;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;

public class HelloRetryRequest {

    public static void main(String[] args) throws Exception {
        CommonConfig config = new CommonConfig();

        // TODO: fix it
        Engine.init()
                .target(config.host())
                .target(config.port())

                // send first ClientHello with empty key_share extension
                .run(new GeneratingClientHello()
                        .supportedVersion(TLSv13)
                        .group(secp256r1)
                        .signatureScheme(ecdsa_secp256r1_sha256)
                        .keyShare(context -> context.factory.createKeyShareForClientHello()))
                .run(new WrappingIntoHandshake()
                        .type(client_hello)
                        .run((context, message) -> context.setFirstClientHello(message)))
                .run(new WrappingIntoTLSPlaintexts()
                        .type(handshake)
                        .version(TLSv12))
                .send(new OutgoingData())

                // receive a HelloRetryRequest
                .require(new IncomingData())
                .run(new ProcessingTLSPlaintext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(server_hello)
                        .run((context, message) -> context.setHelloRetryRequest(message)))
                .run(new ProcessingHelloRetryRequest())

                // send second ClientHello
                .run(new GeneratingClientHello()
                        .supportedVersion(TLSv13)
                        .group(secp256r1)
                        .signatureScheme(ecdsa_secp256r1_sha256)
                        .keyShareEntry(context -> context.negotiator.createKeyShareEntry()))
                .run(new WrappingIntoHandshake()
                        .type(client_hello)
                        .run((context, message) -> context.setSecondClientHello(message)))
                .run(new WrappingIntoTLSPlaintexts()
                        .type(handshake)
                        .version(TLSv12))
                .send(new OutgoingData())

                // receive a ServerHello
                .require(new IncomingData())
                .run(new ProcessingTLSPlaintext()
                        .expect(handshake))
                .run(new ProcessingHandshake()
                        .expect(server_hello)
                        .run((context, message) -> context.setServerHello(message)))
                .connect()
                .run(new NoAlertCheck());
    }

}
