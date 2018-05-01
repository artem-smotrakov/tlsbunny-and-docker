package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GeneratingClientHello extends AbstractAction {

    private ProtocolVersion[] versions = new ProtocolVersion[0];
    private SignatureScheme[] schemes = new SignatureScheme[0];
    private NamedGroup[] groups = new NamedGroup[0];
    private KeyShareEntryFactory[] keyShareEntryFactories = new KeyShareEntryFactory[0];
    private KeyShareFactory[] keyShareFactories = new KeyShareFactory[0];

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

    public GeneratingClientHello keyShareEntry(KeyShareEntryFactory... keyShareEntryFactories) {
        this.keyShareEntryFactories = keyShareEntryFactories;
        return this;
    }

    public GeneratingClientHello keyShare(KeyShareFactory... keyShareFactories) {
        this.keyShareFactories = keyShareFactories;
        return this;
    }

    @Override
    public Action run() throws IOException, NegotiatorException {
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

        for (KeyShareFactory factory : keyShareFactories) {
            extensions.add(wrap(factory.create(context)));
        }

        for (KeyShareEntryFactory factory : keyShareEntryFactories) {
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
        KeyShareEntry create(Context context) throws IOException, NegotiatorException;
    }

    public interface KeyShareFactory {
        KeyShare create(Context context) throws IOException, NegotiatorException;
    }
}
