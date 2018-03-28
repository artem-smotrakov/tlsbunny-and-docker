package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.tls13.utils.Helper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class OutgoingClientHello extends AbstractAction {

    @Override
    public String name() {
        return "sending ClientHello";
    }

    @Override
    public Action run() throws Exception {
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
        TLSPlaintext[] tlsPlaintexts = factory.createTLSPlaintexts(
                ContentType.handshake,
                ProtocolVersion.TLSv10,
                handshake.encoding());

        buffer = Helper.store(tlsPlaintexts);

        if (context.hasFirstClientHello() && context.hasSecondClientHello()) {
            throw new IOException("already received two ClientHello messages");
        }

        if (context.hasFirstClientHello()) {
            context.setSecondClientHello(handshake);
        } else {
            context.setFirstClientHello(handshake);
        }

        return this;
    }
}
