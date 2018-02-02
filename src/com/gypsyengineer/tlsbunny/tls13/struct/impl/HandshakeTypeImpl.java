package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;

public class HandshakeTypeImpl implements HandshakeType {

    private int value;

    HandshakeTypeImpl(int value) {
        this.value = value;
    }

    @Override
    public int encodingLength() {
        return ENCODING_LENGTH;
    }

    @Override
    public byte[] encoding() {
        return ByteBuffer.allocate(ENCODING_LENGTH).put((byte) value).array();
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.value;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HandshakeTypeImpl other = (HandshakeTypeImpl) obj;
        return this.value == other.value;
    }

}
