package com.gypsyengineer.tlsbunny.tls13.client.downgrade;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.client.SingleConnectionClient;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.alert;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.client_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.server_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;

public class CheckDowngradeMessage extends SingleConnectionClient {

    private ProtocolVersion version = TLSv12;

    public static void main(String[] args) {
        try (Output output = new Output()) {
            Config config = SystemPropertiesConfig.load();
            run(output, config, TLSv13);
            run(output, config, TLSv12);
            run(output, config, TLSv11);
            run(output, config, TLSv10);
        }
    }

    public static Client run(Output output, Config config, ProtocolVersion version) {
        return new CheckDowngradeMessage()
                        .expect(version)
                        .set(config)
                        .set(output)
                        .set(StructFactory.getDefault());
    }

    public CheckDowngradeMessage expect(ProtocolVersion version) {
        this.version = version;
        return this;
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
                        .legacyVersion(TLSv12)
                        .groups(secp256r1)
                        .cipherSuites(
                                CipherSuite.TLS_AES_128_GCM_SHA256,
                                factory.createCipherSuite(0x00, 0x2F), // TLS_RSA_WITH_AES_128_CBC_SHA
                                factory.createCipherSuite(0xC0, 0x09), // TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA
                                factory.createCipherSuite(0xC0, 0x13)) // TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA
                        .supportedVersions(version)
                        .signatureSchemes(ecdsa_secp256r1_sha256)
                        .keyShareEntries(context -> context.negotiator.createKeyShareEntry()))
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
                .run(new CheckingDowngradeMessageInServerHello()
                        .expect(version))

                // send an alert
                .run(new GeneratingAlert())
                .run(new WrappingIntoTLSPlaintexts()
                        .type(alert)
                        .version(TLSv12))
                .send(new OutgoingData());
    }

}
