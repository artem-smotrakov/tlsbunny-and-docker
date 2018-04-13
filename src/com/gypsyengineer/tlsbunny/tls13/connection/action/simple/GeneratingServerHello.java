package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.nio.ByteBuffer;
import java.util.List;

public class GeneratingServerHello extends AbstractAction {

    @Override
    public String name() {
        return "generating ServerHello";
    }

    @Override
    public Action run() throws Exception {
        List<Extension> extensions = List.of(
                wrap(factory.createSupportedVersionForClientHello(ProtocolVersion.TLSv13)),
                wrap(factory.createSignatureSchemeList(scheme)),
                wrap(factory.createNamedGroupList(group)),
                wrap(factory.createKeyShareForClientHello(negotiator.createKeyShareEntry())));

        ServerHello hello = factory.createServerHello(
                ProtocolVersion.TLSv12,
                createRandom(),
                StructFactory.EMPTY_SESSION_ID,
                CipherSuite.TLS_AES_128_GCM_SHA256,
                factory.createCompressionMethod(0),
                extensions);

        out = ByteBuffer.wrap(hello.encoding());

        return this;
    }
}
