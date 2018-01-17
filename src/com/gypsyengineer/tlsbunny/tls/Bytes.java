package com.gypsyengineer.tlsbunny.tls;

import com.gypsyengineer.tlsbunny.tls.Entity;

public class Bytes implements Entity {

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
}
