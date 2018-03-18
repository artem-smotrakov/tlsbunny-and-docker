package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;

public class ContentTypeImpl implements ContentType {

    private final int code;

    ContentTypeImpl(int code) {
        check(code);
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public boolean isHandshake() {
        return code == handshake.getCode();
    }

    @Override
    public boolean isApplicationData() {
        return code == application_data.getCode();
    }

    @Override
    public boolean isAlert() {
        return code == alert.getCode();
    }

    @Override
    public boolean isChangeCipherSpec() {
        return code == change_cipher_spec.getCode();
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

    private static void check(int code) {
        if (code < 0 || code > 255) {
            throw new IllegalArgumentException();
        }
    }

}
