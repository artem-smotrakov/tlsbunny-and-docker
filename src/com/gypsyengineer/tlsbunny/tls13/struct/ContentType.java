package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;
import java.nio.ByteBuffer;

public class ContentType implements Entity {

    public static final int ENCODING_LENGTH = 1;
    
    public static final ContentType INVALID = new ContentType(0);
    public static final ContentType ALERT = new ContentType(21);
    public static final ContentType HANDSHAKE = new ContentType(22);
    public static final ContentType APPLICATION_DATA = new ContentType(23);

    private int code;

    public ContentType(int code) {
        check(code);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        check(code);
        this.code = code;
    }

    public boolean isHandshake() {
        return code == HANDSHAKE.code;
    }

    public boolean isApplicationData() {
        return code == APPLICATION_DATA.code;
    }

    public boolean isAlert() {
        return code == ALERT.code;
    }

    @Override
    public int encodingLength() {
        return ENCODING_LENGTH;
    }

    @Override
    public byte[] encoding() {
        return new byte[] { (byte) code };
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.code;
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
        final ContentType other = (ContentType) obj;
        return this.code == other.code;
    }

    public static ContentType parse(ByteBuffer data) {
        return new ContentType(data.get() & 0xFF);
    }
    
    private static void check(int code) {
        if (code < 0 || code > 255) {
            throw new IllegalArgumentException();
        }
    }

}
