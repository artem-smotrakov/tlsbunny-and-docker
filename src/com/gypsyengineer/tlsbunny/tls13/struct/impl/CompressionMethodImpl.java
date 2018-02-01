package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.CompressionMethod;

public class CompressionMethodImpl implements CompressionMethod {


    private int code;

    private CompressionMethodImpl(int code) {
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

    public static CompressionMethodImpl createNull() {
        return new CompressionMethodImpl(0);
    }

    public static CompressionMethodImpl parse(ByteBuffer buffer) {
        return new CompressionMethodImpl(buffer.get() & 0xFF);
    }

    private static void check(int code) {
        if (code < 0 || code > 255) {
            throw new IllegalArgumentException();
        }
    }

}
