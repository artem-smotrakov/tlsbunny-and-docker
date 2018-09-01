package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GeneratingServerHello extends AbstractAction {

    private static final byte[] downgrade_tls12_message = new byte[] {
            0x44, 0x4F, 0x57, 0x4E, 0x47, 0x52, 0x44, 0x01
    };
    private static final byte[] downgrade_tls11_and_below_message = new byte[] {
            0x44, 0x4F, 0x57, 0x4E, 0x47, 0x52, 0x44, 0x00
    };

    private ProtocolVersion[] versions = new ProtocolVersion[0];
    private SignatureScheme[] schemes = new SignatureScheme[0];
    private NamedGroup[] groups = new NamedGroup[0];
    private KeyShareEntryFactory[] keyShareEntryFactories = new KeyShareEntryFactory[0];
    private KeyShareFactory[] keyShareFactories = new KeyShareFactory[0];
    private byte[] downgradeMessage = null;

    @Override
    public String name() {
        return "generating ServerHello";
    }

    public GeneratingServerHello downgradeTLSv12() {
        downgradeMessage = downgrade_tls12_message;
        return this;
    }

    public GeneratingServerHello downgradeBelowTLSv12() {
        downgradeMessage = downgrade_tls11_and_below_message;
        return this;
    }

    public GeneratingServerHello supportedVersion(ProtocolVersion... versions) {
        this.versions = versions;
        return this;
    }

    public GeneratingServerHello signatureScheme(SignatureScheme... schemes) {
        this.schemes = schemes;
        return this;
    }

    public GeneratingServerHello group(NamedGroup... groups) {
        this.groups = groups;
        return this;
    }

    public GeneratingServerHello keyShareEntry(KeyShareEntryFactory... keyShareEntryFactories) {
        this.keyShareEntryFactories = keyShareEntryFactories;
        return this;
    }

    public GeneratingServerHello keyShare(KeyShareFactory... keyShareFactories) {
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

        extensions.add(wrap(context.factory.createNamedGroupList(groups)));

        for (KeyShareFactory factory : keyShareFactories) {
            extensions.add(wrap(factory.create(context)));
        }

        for (KeyShareEntryFactory factory : keyShareEntryFactories) {
            extensions.add(wrap(context.factory.createKeyShareForServerHello(
                    factory.create(context))));
        }

        Random random = createRandom();
        if (downgradeMessage != null) {
            random.setLastBytes(downgradeMessage);
        }

        ServerHello hello = context.factory.createServerHello(
                ProtocolVersion.TLSv12,
                random,
                StructFactory.EMPTY_SESSION_ID,
                CipherSuite.TLS_AES_128_GCM_SHA256,
                context.factory.createCompressionMethod(0),
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
