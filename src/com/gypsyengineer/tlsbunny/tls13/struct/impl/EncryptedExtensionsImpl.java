package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.EncryptedExtensions;
import java.io.IOException;
import java.nio.ByteBuffer;

public class EncryptedExtensionsImpl implements EncryptedExtensions {


    private Vector<ExtensionImpl> extensions;

    EncryptedExtensionsImpl(Vector<ExtensionImpl> extensions) {
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
    public Vector<ExtensionImpl> getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(Vector<ExtensionImpl> extensions) {
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
