package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GeneratingClientHello extends AbstractAction {

    private ProtocolVersion[] versions;
    private SignatureScheme[] schemes;
    private NamedGroup[] groups;
    private KeyShareEntryFactory[] factories;

    @Override
    public String name() {
        return "generating ClientHello";
    }

    public GeneratingClientHello supportedVersion(ProtocolVersion... versions) {
        this.versions = versions;
        return this;
    }

    public GeneratingClientHello signatureScheme(SignatureScheme... schemes) {
        this.schemes = schemes;
        return this;
    }

    public GeneratingClientHello group(NamedGroup... groups) {
        this.groups = groups;
        return this;
    }

    public GeneratingClientHello keyShare(KeyShareEntryFactory... factories) {
        this.factories = factories;
        return this;
    }

    @Override
    public Action run() throws Exception {
        List<Extension> extensions = new ArrayList<>();

        for (ProtocolVersion version : versions) {
            extensions.add(wrap(context.factory.createSupportedVersionForClientHello(version)));
        }

        for (SignatureScheme scheme : schemes) {
            extensions.add(wrap(context.factory.createSignatureSchemeList(scheme)));
        }

        for (NamedGroup group : groups) {
            extensions.add(wrap(context.factory.createNamedGroupList(group)));
        }

        for (KeyShareEntryFactory factory : factories) {
            extensions.add(wrap(context.factory.createKeyShareForClientHello(
                    factory.create(context))));
        }

        ClientHello hello = context.factory.createClientHello(ProtocolVersion.TLSv12,
                createRandom(),
                StructFactory.EMPTY_SESSION_ID,
                List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                List.of(context.factory.createCompressionMethod(0)),
                extensions);

        out = ByteBuffer.wrap(hello.encoding());

        return this;
    }

    public interface KeyShareEntryFactory {
        KeyShareEntry create(Context context) throws Exception;
    }
}
