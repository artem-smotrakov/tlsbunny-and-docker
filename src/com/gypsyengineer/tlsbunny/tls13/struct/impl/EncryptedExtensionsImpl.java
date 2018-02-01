package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.EncryptedExtensions;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import java.io.IOException;
import java.nio.ByteBuffer;

public class EncryptedExtensionsImpl implements EncryptedExtensions {

    private Vector<Extension> extensions;

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
    public HandshakeTypeImpl type() {
        return HandshakeTypeImpl.encrypted_extensions;
    }

    @Override
    public Vector<Extension> getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(Vector<Extension> extensions) {
        this.extensions = extensions;
    }
    
    public static EncryptedExtensionsImpl parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }

    public static EncryptedExtensionsImpl parse(ByteBuffer buffer) {
        return new EncryptedExtensionsImpl(
                Vector.parse(
                    buffer,
                    EXTENSIONS_LENGTH_BYTES,
                    buf -> ExtensionImpl.parse(buf)));
    }

}
