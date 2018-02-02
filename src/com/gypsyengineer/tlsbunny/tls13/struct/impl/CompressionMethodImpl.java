package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls13.struct.CompressionMethod;

public class CompressionMethodImpl implements CompressionMethod {

    private int code;

    CompressionMethodImpl(int code) {
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
    public byte[] encoding() {
        return new byte[] { (byte) code };
    }

    private static void check(int code) {
        if (code < 0 || code > 255) {
            throw new IllegalArgumentException();
        }
    }

}
