package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ProtocolVersion implements Entity {

    public static final int ENCODING_LENGTH = 2;
    public static final ProtocolVersion SSLv3  = new ProtocolVersion(3, 0);
    public static final ProtocolVersion TLSv10 = new ProtocolVersion(3, 1);
    public static final ProtocolVersion TLSv11 = new ProtocolVersion(3, 2);
    public static final ProtocolVersion TLSv12 = new ProtocolVersion(3, 3);
    public static final ProtocolVersion TLSv13 = new ProtocolVersion(3, 4);

    private final int major;
    private final int minor;

    public ProtocolVersion(int major, int minor) {
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
        final ProtocolVersion other = (ProtocolVersion) obj;
        if (this.major != other.major) {
            return false;
        }
        return this.minor == other.minor;
    }

    public static ProtocolVersion parse(ByteBuffer data) {
        return new ProtocolVersion(data.get() & 0xFF, data.get() & 0xFF);
    }
    
    private static void check(int minor, int major) {
        if (major < 0 || major > 255 || minor < 0 || minor > 255) {
            throw new IllegalArgumentException();
        }
    }
    
}
