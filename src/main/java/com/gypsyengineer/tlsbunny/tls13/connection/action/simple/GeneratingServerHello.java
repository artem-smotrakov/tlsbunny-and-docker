package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class GeneratingServerHello extends AbstractAction {

    @Override
    public String name() {
        return "generating ServerHello";
    }

    @Override
    public Action run() throws IOException, NegotiatorException {
        List<Extension> extensions = List.of(
                wrap(context.factory.createSupportedVersionForClientHello(ProtocolVersion.TLSv13)),
                wrap(context.factory.createSignatureSchemeList(context.scheme)),
                wrap(context.factory.createNamedGroupList(context.group)),
                wrap(context.factory.createKeyShareForClientHello(context.negotiator.createKeyShareEntry())));

        ServerHello hello = context.factory.createServerHello(
                ProtocolVersion.TLSv12,
                createRandom(),
                StructFactory.EMPTY_SESSION_ID,
                CipherSuite.TLS_AES_128_GCM_SHA256,
                context.factory.createCompressionMethod(0),
                extensions);

        out = ByteBuffer.wrap(hello.encoding());

        return this;
    }
}
