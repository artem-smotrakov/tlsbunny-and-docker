package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Connection;
import java.io.IOException;

public abstract class AbstractAction implements Action {

    static final long DEFAULT_SEED = 0;
    static final long SEED = Long.getLong("tlsbunny.seed", DEFAULT_SEED);

    boolean succeeded = false;

    Connection connection;
    byte[] data;
    StructFactory factory;
    SignatureScheme scheme;
    NamedGroup group;
    CipherSuite suite;
    Negotiator negotiator;
    HKDF hkdf;
    Context context;

    @Override
    public Action set(StructFactory factory) {
        this.factory = factory;
        return this;
    }

    @Override
    public Action set(SignatureScheme scheme) {
        this.scheme = scheme;
        return this;
    }

    @Override
    public Action set(NamedGroup group) {
        this.group = group;
        return this;
    }

    @Override
    public Action set(CipherSuite suite) {
        this.suite = suite;
        return this;
    }

    @Override
    public Action set(Negotiator negotiator) {
        this.negotiator = negotiator;
        return this;
    }

    @Override
    public Action set(HKDF hkdf) {
        this.hkdf = hkdf;
        return this;
    }

    @Override
    public Action set(Context context) {
        this.context = context;
        return this;
    }

    @Override
    public Action set(byte[] data) {
        this.data = data;
        return this;
    }

    @Override
    public Action set(Connection connection) {
        this.connection = connection;
        return this;
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
