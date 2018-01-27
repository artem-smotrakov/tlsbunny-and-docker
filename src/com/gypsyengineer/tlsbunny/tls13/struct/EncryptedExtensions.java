package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;
import java.nio.ByteBuffer;

public class EncryptedExtensions implements HandshakeMessage {

    public static final int EXTENSIONS_LENGTH_BYTES = 2;

    private Vector<Extension> extensions;

    private EncryptedExtensions(Vector<Extension> extensions) {
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
        return HandshakeType.encrypted_extensions;
    }

    public Vector<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(Vector<Extension> extensions) {
        this.extensions = extensions;
    }
    
    public static EncryptedExtensions parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }

    public static EncryptedExtensions parse(ByteBuffer buffer) {
        return new EncryptedExtensions(
                Vector.parse(
                    buffer,
                    EXTENSIONS_LENGTH_BYTES,
                    buf -> Extension.parse(buf)));
    }

}
