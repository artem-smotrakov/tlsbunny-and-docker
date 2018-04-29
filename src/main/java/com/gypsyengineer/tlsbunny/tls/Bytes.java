package com.gypsyengineer.tlsbunny.tls;

import java.nio.ByteBuffer;

public class Bytes implements Struct {

    public static final Bytes EMPTY = new Bytes(new byte[0]);

    private final byte[] bytes;

    public Bytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public int encodingLength() {
        return bytes.length;
    }

    @Override
    public byte[] encoding() {
        return bytes;
    }
    
    public static Bytes parse(ByteBuffer buffer, int length) {
        byte[] body = new byte[length];
        buffer.get(body);
        
        return new Bytes(body);
    }
}
