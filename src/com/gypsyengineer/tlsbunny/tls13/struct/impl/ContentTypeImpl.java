package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;

public class ContentTypeImpl implements ContentType {


    private int code;

    public ContentTypeImpl(int code) {
        check(code);
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public void setCode(int code) {
        check(code);
        this.code = code;
    }

    @Override
    public boolean isHandshake() {
        return code == handshake.code;
    }

    @Override
    public boolean isApplicationData() {
        return code == application_data.code;
    }

    @Override
    public boolean isAlert() {
        return code == alert.code;
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
        final ContentTypeImpl other = (ContentTypeImpl) obj;
        return this.code == other.code;
    }

    public static ContentTypeImpl parse(ByteBuffer data) {
        return new ContentTypeImpl(data.get() & 0xFF);
    }
    
    private static void check(int code) {
        if (code < 0 || code > 255) {
            throw new IllegalArgumentException();
        }
    }

}
