package com.gypsyengineer.tlsbunny.tls;

import java.nio.ByteBuffer;

public class UInt16 implements Entity {

    public static final int ENCODING_LENGTH = 2;
    public static final int MAX = 65535;
    public static final int MIN = 0;

    public final int value;

    public UInt16(int value) {
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
        return ByteBuffer.allocate(ENCODING_LENGTH).putShort((short) value).array();
    }

    public static UInt16 parse(ByteBuffer data) {
        return new UInt16(data.getShort() & 0xFFFF);
    }
    
}
