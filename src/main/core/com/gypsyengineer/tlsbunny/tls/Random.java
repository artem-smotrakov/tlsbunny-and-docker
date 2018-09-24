package com.gypsyengineer.tlsbunny.tls;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class Random implements Struct {

    public static final int LENGTH = 32;

    private byte[] bytes;

    public Random(byte[] bytes) {
        if (bytes.length != LENGTH) {
            throw new IllegalArgumentException();
        }
        
        this.bytes = bytes.clone();
    }

    public Random() {
        this(new byte[LENGTH]);
    }

    public byte[] getBytes() {
        return bytes.clone();
    }

    public void setBytes(byte[] bytes) {
        if (bytes.length != LENGTH) {
            throw new IllegalArgumentException();
        }
        
        this.bytes = bytes.clone();
    }

    @Override
    public int encodingLength() {
        return LENGTH;
    }

    @Override
    public byte[] encoding() throws IOException {
        return ByteBuffer.allocate(LENGTH).put(bytes).array();
    }

    public void setLastBytes(byte[] lastBytes) {
        if (lastBytes == null) {
            throw whatTheHell("bytes is null!");
        }

        if (lastBytes.length > bytes.length) {
            throw whatTheHell("it's too long!");
        }

        int i = 0;
        int j = bytes.length - lastBytes.length;
        while (i < lastBytes.length) {
            bytes[j++] = lastBytes[i++];
        }
    }

    public static Random parse(ByteBuffer buffer) {
        byte[] bytes = new byte[LENGTH];
        buffer.get(bytes);
        return new Random(bytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Random random = (Random) o;
        return Arrays.equals(bytes, random.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
