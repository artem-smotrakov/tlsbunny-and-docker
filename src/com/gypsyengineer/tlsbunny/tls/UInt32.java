package com.gypsyengineer.tlsbunny.tls;

import java.nio.ByteBuffer;

public class UInt32 implements Entity {

    public static final int ENCODING_LENGTH = 4;
    public static final int MAX = Integer.MAX_VALUE;
    public static final int MIN = 0;

    public final int value;

    public UInt32(int value) {
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
        return ByteBuffer.allocate(ENCODING_LENGTH).putInt(value).array();
    }

    public static UInt32 parse(ByteBuffer data) {
        return new UInt32(data.getInt() & 0xFFFFFFFF);
    }

}
