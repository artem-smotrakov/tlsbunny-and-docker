package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Entity;
import java.nio.ByteBuffer;

public class HandshakeType implements Entity {

    public static final int ENCODING_LENGTH = 1;

    public static final HandshakeType cleint_hello          = new HandshakeType(1);
    public static final HandshakeType server_hello          = new HandshakeType(2);
    public static final HandshakeType new_session_ticket    = new HandshakeType(4);
    public static final HandshakeType end_of_early_data     = new HandshakeType(5);
    public static final HandshakeType hello_retry_request   = new HandshakeType(6);
    public static final HandshakeType encrypted_extensions  = new HandshakeType(8);
    public static final HandshakeType certificate           = new HandshakeType(11);
    public static final HandshakeType certificate_request   = new HandshakeType(13);
    public static final HandshakeType certificate_verify    = new HandshakeType(15);
    public static final HandshakeType finished              = new HandshakeType(20);
    public static final HandshakeType key_update            = new HandshakeType(24);
    public static final HandshakeType message_hash          = new HandshakeType(254);

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
