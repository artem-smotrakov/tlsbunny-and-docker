package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.EncryptedExtensions;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import java.io.IOException;

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
    public HandshakeType type() {
        return HandshakeTypeImpl.encrypted_extensions;
    }

    @Override
    public Vector<Extension> getExtensions() {
        return extensions;
    }

}
