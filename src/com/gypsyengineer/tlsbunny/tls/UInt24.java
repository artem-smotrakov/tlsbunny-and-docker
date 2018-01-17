package com.gypsyengineer.tlsbunny.tls;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class UInt24 implements Entity {

    public static final int ENCODING_LENGTH = 3;
    public static final int BASE_POW_1 = 256;
    public static final int BASE_POW_2 = BASE_POW_1 * BASE_POW_1;
    public static final int MAX = 16777215;
    public static final int MIN = 0;
    public static final UInt24 ZERO = new UInt24(0);

    public final int value;

    public UInt24(int value) {
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
        byte[] array = ByteBuffer.allocate(4).putInt(value).array();
        return Arrays.copyOfRange(array, 1, array.length);
    }

    public static UInt24 parse(byte[] data) {
        return parse(ByteBuffer.wrap(data));
    }

    public static UInt24 parse(ByteBuffer data) {
        int value = (data.get() & 0xFF) * BASE_POW_2
                + (data.get() & 0xFF) * BASE_POW_1
                + (data.get() & 0xFF);
        
        return new UInt24(value);
    }

}
