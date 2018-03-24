package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.*;
import java.util.List;

public class OutgoingClientHello extends AbstractAction {

    @Override
    public String description() {
        return "send ClientHello";
    }

    @Override
    public Action run() {
        try {
            List<Extension> extensions = List.of(
                    wrap(factory.createSupportedVersionForClientHello(ProtocolVersion.TLSv13)),
                    wrap(factory.createSignatureSchemeList(scheme)),
                    wrap(factory.createNamedGroupList(group)),
                    wrap(factory.createKeyShareForClientHello(negotiator.createKeyShareEntry())));

            ClientHello hello = factory.createClientHello(ProtocolVersion.TLSv12,
                    createRandom(),
                    StructFactory.EMPTY_SESSION_ID,
                    List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                    List.of(factory.createCompressionMethod(0)),
                    extensions);

            Handshake handshake = toHandshake(hello);
            connection.send(factory.createTLSPlaintexts(ContentType.handshake,
                    ProtocolVersion.TLSv10,
                    handshake.encoding()));

            // TODO: check if context already has the first client hello message
            context.setFirstClientHello(handshake);

            succeeded = true;
        } catch (Exception e) {
            succeeded = false;
        }

        return this;
    }
}
