package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;

public class ProtocolVersionImpl implements ProtocolVersion {

    private final int major;
    private final int minor;

    ProtocolVersionImpl(int major, int minor) {
        check(minor, major);
        this.major = major;
        this.minor = minor;
    }

    @Override
    public int encodingLength() {
        return ENCODING_LENGTH;
    }

    @Override
    public byte[] encoding() throws IOException {
        return ByteBuffer.allocate(2).put((byte) major).put((byte) minor).array();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.major;
        hash = 17 * hash + this.minor;
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
        final ProtocolVersionImpl other = (ProtocolVersionImpl) obj;
        if (this.major != other.major) {
            return false;
        }
        return this.minor == other.minor;
    }

    private static void check(int minor, int major) {
        if (major < 0 || major > 255 || minor < 0 || minor > 255) {
            throw new IllegalArgumentException();
        }
    }
    
}
