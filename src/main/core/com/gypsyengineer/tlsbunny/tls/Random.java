package com.gypsyengineer.tlsbunny.tls;

import java.nio.ByteBuffer;

public interface Random extends Struct {

    int LENGTH = 32;

    byte[] getBytes();
    void setBytes(byte[] bytes);
    void setLastBytes(byte[] lastBytes);

    static Random parse(ByteBuffer buffer) {
        byte[] bytes = new byte[LENGTH];
        buffer.get(bytes);
        return new RandomImpl(bytes);
    }

    static Random create() {
        return new RandomImpl();
    }
}
