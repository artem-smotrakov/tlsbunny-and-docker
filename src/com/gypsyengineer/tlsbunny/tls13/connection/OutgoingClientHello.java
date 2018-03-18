package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.*;
import java.nio.ByteBuffer;
import java.util.List;

public class OutgoingClientHello extends AbstractAction {

    // TODO: do we really need buffer in outgoing actions? what can we do here?
    @Override
    boolean runImpl(ByteBuffer buffer) throws Exception {
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

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
