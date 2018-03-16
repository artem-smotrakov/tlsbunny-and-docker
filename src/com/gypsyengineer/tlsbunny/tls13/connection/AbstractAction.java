package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Connection;

import java.io.IOException;

public abstract class AbstractAction implements Action {

    static final long DEFAULT_SEED = 0;
    static final long SEED = Long.getLong("tlsbunny.seed", DEFAULT_SEED);

    byte[] data;
    Connection connection;
    StructFactory factory;
    boolean succeeded = false;
    SignatureScheme scheme;
    NamedGroup group;
    Negotiator negotiator;
    Context context;

    @Override
    public void init(StructFactory factory) {
        this.factory = factory;
    }

    @Override
    public void init(SignatureScheme scheme) {
        this.scheme = scheme;
    }

    @Override
    public void init(NamedGroup group) {
        this.group = group;
    }

    @Override
    public void init(Negotiator negotiator) {
        this.negotiator = negotiator;
    }

    @Override
    public void init(Context context) {
        this.context = context;
    }

    @Override
    public void init(byte[] data) {
        this.data = data;
    }

    @Override
    public void init(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean succeeded() {
        return succeeded;
    }

    @Override
    public byte[] data() {
        return data.clone();
    }

    // helper methods

    Handshake toHandshake(HandshakeMessage message) throws IOException {
        return factory.createHandshake(message.type(), message.encoding());
    }

    Extension wrap(SupportedVersions supportedVersions) throws IOException {
        return factory.createExtension(
                ExtensionType.supported_versions, supportedVersions.encoding());
    }

    Extension wrap(SignatureSchemeList signatureSchemeList) throws IOException {
        return factory.createExtension(
                ExtensionType.signature_algorithms, signatureSchemeList.encoding());
    }

    Extension wrap(NamedGroupList namedGroupList) throws IOException {
        return factory.createExtension(
                ExtensionType.supported_groups, namedGroupList.encoding());
    }

    Extension wrap(KeyShare keyShare) throws IOException {
        return factory.createExtension(
                ExtensionType.key_share, keyShare.encoding());
    }

    static Random createRandom() {
        java.util.Random generator = new java.util.Random(SEED);
        byte[] random_bytes = new byte[Random.LENGTH];
        generator.nextBytes(random_bytes);
        Random random = new Random();
        random.setBytes(random_bytes);

        return random;
    }
}
