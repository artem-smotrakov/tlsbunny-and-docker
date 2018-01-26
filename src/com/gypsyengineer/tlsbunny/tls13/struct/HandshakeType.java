package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;
import java.nio.ByteBuffer;

public class HandshakeType implements Entity {

    public static final int ENCODING_LENGTH = 1;

    public static final HandshakeType CLIENT_HELLO          = new HandshakeType(1);
    public static final HandshakeType SERVER_HELLO          = new HandshakeType(2);
    public static final HandshakeType NEW_SESSION_TICKET    = new HandshakeType(4);
    public static final HandshakeType END_OF_EARLY_DATA     = new HandshakeType(5);
    public static final HandshakeType HELLO_RETRY_REQUEST   = new HandshakeType(6);
    public static final HandshakeType ENCRYPTED_EXTENSIONS  = new HandshakeType(8);
    public static final HandshakeType CERTIFICATE           = new HandshakeType(11);
    public static final HandshakeType CERTIFICATE_REQUEST   = new HandshakeType(13);
    public static final HandshakeType CERTIFICATE_VERIFY    = new HandshakeType(15);
    public static final HandshakeType FINISHED              = new HandshakeType(20);
    public static final HandshakeType KEY_UPDATE            = new HandshakeType(24);
    public static final HandshakeType MESSAGE_HASH          = new HandshakeType(254);

    private int value;

    public HandshakeType(int value) {
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static HandshakeType parse(ByteBuffer data) {
        return new HandshakeType(data.get() & 0xFF);
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
        final HandshakeType other = (HandshakeType) obj;
        return this.value == other.value;
    }

}
