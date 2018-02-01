package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;

public class SignatureSchemeImpl implements SignatureScheme {


    private int code;

    public SignatureSchemeImpl(int code) {
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
    public int encodingLength() {
        return ENCODING_LENGTH;
    }

    @Override
    public byte[] encoding() throws IOException {
        return ByteBuffer.allocate(ENCODING_LENGTH).putShort((short) code).array();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + this.code;
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
        final SignatureSchemeImpl other = (SignatureSchemeImpl) obj;
        return this.code == other.code;
    }

    public static SignatureSchemeImpl parse(ByteBuffer buffer) {
        return new SignatureSchemeImpl(buffer.getShort());
    }

    private static void check(int code) {
        if (code < 0 || code > 65535) {
            throw new IllegalArgumentException();
        }
    }
}
