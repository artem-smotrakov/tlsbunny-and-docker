package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.UncompressedPointRepresentation;

public class UncompressedPointRepresentationImpl implements UncompressedPointRepresentation {

    private final byte legacy_form = 4;
    private final byte[] X;
    private final byte[] Y;

    public UncompressedPointRepresentationImpl(byte[] X, byte[] Y) {
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
    
    public static UncompressedPointRepresentationImpl parse(
            byte[] bytes, int coordinate_length) {

        return parse(ByteBuffer.wrap(bytes), coordinate_length);
    }

    public static UncompressedPointRepresentationImpl parse(
            ByteBuffer buffer, int coordinate_length) {

        byte legacy_form = buffer.get();
        if (legacy_form != 4) {
            throw new IllegalArgumentException();
        }

        byte[] X = new byte[coordinate_length];
        byte[] Y = new byte[coordinate_length];
        buffer.get(X);
        buffer.get(Y);

        return new UncompressedPointRepresentationImpl(X, Y);
    }

}
