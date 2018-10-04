package com.gypsyengineer.tlsbunny.tls;

import java.nio.ByteBuffer;

public class UInt8 implements Struct {

    public static final int ENCODING_LENGTH = 1;
    public static final int MAX = 255;
    public static final int MIN = 0;
    public static final UInt8 ZERO = new UInt8(0);

    public final int value;

    public UInt8(int value) {
        if (value < MIN || value > MAX) {
            throw new IllegalArgumentException();
        }

        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int encodingLength() {
        return ENCODING_LENGTH;
    }

    @Override
    public byte[] encoding() {
        return new byte[] { (byte) value };
    }

    @Override
    public UInt8 copy() {
        return new UInt8(value);
    }

    public static UInt8 parse(ByteBuffer data) {
        return new UInt8(data.get() & 0xFF);
    }

}
