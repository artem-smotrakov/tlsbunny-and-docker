package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.EncryptedExtensions;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import java.io.IOException;
import java.util.Objects;

public class EncryptedExtensionsImpl implements EncryptedExtensions {

    private final Vector<Extension> extensions;

    EncryptedExtensionsImpl(Vector<Extension> extensions) {
        this.extensions = extensions;
    }

    @Override
    public int encodingLength() {
        return extensions.encodingLength();
    }

    @Override
    public byte[] encoding() throws IOException {
        return extensions.encoding();
    }

    @Override
    public Struct copy() {
        return new EncryptedExtensionsImpl((Vector<Extension>) extensions.copy());
    }

    @Override
    public HandshakeType type() {
        return HandshakeTypeImpl.encrypted_extensions;
    }

    @Override
    public Vector<Extension> extensions() {
        return extensions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EncryptedExtensionsImpl that = (EncryptedExtensionsImpl) o;
        return Objects.equals(extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extensions);
    }
}
