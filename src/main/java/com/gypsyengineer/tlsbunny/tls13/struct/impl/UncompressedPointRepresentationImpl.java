package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls13.struct.UncompressedPointRepresentation;

public class UncompressedPointRepresentationImpl implements UncompressedPointRepresentation {

    private final byte legacy_form = 4;
    private final byte[] X;
    private final byte[] Y;

    UncompressedPointRepresentationImpl(byte[] X, byte[] Y) {
        this.X = X;
        this.Y = Y;
    }

    @Override
    public byte[] getX() {
        return X;
    }

    @Override
    public byte[] getY() {
        return Y;
    }

    @Override
    public int encodingLength() {
        return 1 + X.length + Y.length;
    }

    @Override
    public byte[] encoding() {
        return ByteBuffer.allocate(encodingLength())
            .put(legacy_form)
            .put(X)
            .put(Y)
            .array();
    }
    
}
