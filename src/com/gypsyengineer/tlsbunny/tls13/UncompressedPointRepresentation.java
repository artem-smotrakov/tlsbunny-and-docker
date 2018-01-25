package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;
import java.io.IOException;
import java.nio.ByteBuffer;

public class UncompressedPointRepresentation implements Entity {

    private final byte legacy_form = 4;
    private final byte[] X;
    private final byte[] Y;

    public UncompressedPointRepresentation(byte[] X, byte[] Y) {
        this.X = X;
        this.Y = Y;
    }

    public byte[] getX() {
        return X;
    }

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
    
    public static UncompressedPointRepresentation parse(
            byte[] bytes, int coordinate_length) {

        return parse(ByteBuffer.wrap(bytes), coordinate_length);
    }

    public static UncompressedPointRepresentation parse(
            ByteBuffer buffer, int coordinate_length) {

        byte legacy_form = buffer.get();
        if (legacy_form != 4) {
            throw new IllegalArgumentException();
        }

        byte[] X = new byte[coordinate_length];
        byte[] Y = new byte[coordinate_length];
        buffer.get(X);
        buffer.get(Y);

        return new UncompressedPointRepresentation(X, Y);
    }

}
