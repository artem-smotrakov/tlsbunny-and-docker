package com.gypsyengineer.tlsbunny.tls;

import java.nio.ByteBuffer;

public class UInt32 implements Struct {

    public static final int ENCODING_LENGTH = 4;

    public final long value;

    public UInt32(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public int encodingLength() {
        return ENCODING_LENGTH;
    }
    
    @Override
    public byte[] encoding() {
        return ByteBuffer.allocate(ENCODING_LENGTH).putLong(value).array();
    }

    public static UInt32 parse(ByteBuffer data) {
        return new UInt32(data.getLong() & 0xFFFFFFFF);
    }

}
