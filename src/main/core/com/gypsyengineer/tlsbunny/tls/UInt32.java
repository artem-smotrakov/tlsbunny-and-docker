package com.gypsyengineer.tlsbunny.tls;

import java.nio.ByteBuffer;

public class UInt32 implements Struct {

    public static final int ENCODING_LENGTH = 4;

    public final byte[] value;

    private UInt32(byte[] value) {
        this.value = value;
    }

    @Override
    public int encodingLength() {
        return ENCODING_LENGTH;
    }
    
    @Override
    public byte[] encoding() {
        return ByteBuffer.allocate(ENCODING_LENGTH).put(value).array();
    }

    @Override
    public UInt32 copy() {
        return new UInt32(value);
    }

    public static UInt32 parse(ByteBuffer data) {
        byte[] value = new byte[ENCODING_LENGTH];
        data.get(value);
        return new UInt32(value);
    }

}
