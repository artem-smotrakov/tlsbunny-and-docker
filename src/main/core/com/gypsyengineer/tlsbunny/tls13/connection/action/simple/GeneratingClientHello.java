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

    public static final byte[] NO_COOKIE = null;
    public static final MaxFragmentLength NO_MAX_FRAGMENT_LENGTH = null;

    private ProtocolVersion legacyVersion = ProtocolVersion.TLSv12;
    private ProtocolVersion[] versions = new ProtocolVersion[0];
    private SignatureScheme[] schemes = new SignatureScheme[0];
    private NamedGroup[] groups = new NamedGroup[0];
    private KeyShareEntryFactory[] keyShareEntryFactories = new KeyShareEntryFactory[0];
    private KeyShareFactory[] keyShareFactories = new KeyShareFactory[0];
    private byte[] cookie = NO_COOKIE;
    private MaxFragmentLength maxFragmentLength = NO_MAX_FRAGMENT_LENGTH;

    @Override
    public String name() {
        return "generating ClientHello";
    }

    public GeneratingClientHello legacyVersion(ProtocolVersion legacyVersion) {
        this.legacyVersion = legacyVersion;
        return this;
    }

    public GeneratingClientHello supportedVersions(ProtocolVersion... versions) {
        this.versions = versions;
        return this;
    }

    public GeneratingClientHello signatureSchemes(SignatureScheme... schemes) {
        this.schemes = schemes;
        return this;
    }

    public GeneratingClientHello groups(NamedGroup... groups) {
        this.groups = groups;
        return this;
    }

    public GeneratingClientHello keyShareEntries(
            KeyShareEntryFactory... keyShareEntryFactories) {

        this.keyShareEntryFactories = keyShareEntryFactories;
        return this;
    }

    public GeneratingClientHello keyShares(KeyShareFactory... keyShareFactories) {
        this.keyShareFactories = keyShareFactories;
        return this;
    }

    public GeneratingClientHello cookie(byte[] cookie) {
        this.cookie = cookie.clone();
        return this;
    }

    public GeneratingClientHello maxFragmentLength(int code) {
        this.maxFragmentLength = context.factory.createMaxFragmentLength(code);
        return this;
    }

    public GeneratingClientHello set(MaxFragmentLength maxFragmentLength) {
        this.maxFragmentLength = maxFragmentLength;
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
            extensions.add(wrap(context.factory.createKeyShareForClientHello(
                    factory.create(context))));
        }

        if (cookie != NO_COOKIE) {
            extensions.add(wrap(context.factory.createCookie(cookie)));
        }

        if (maxFragmentLength != NO_MAX_FRAGMENT_LENGTH) {
            extensions.add(wrap(maxFragmentLength));
        }

        ClientHello hello = context.factory.createClientHello(
                legacyVersion,
                createRandom(),
                StructFactory.EMPTY_SESSION_ID,
                List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                List.of(CompressionMethod.None),
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