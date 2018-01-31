package com.gypsyengineer.tlsbunny.tls13.struct;

import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;

public class CompressionMethod implements Struct {

    public static final int ENCODING_LENGTH = 1;

    private int code;

    private CompressionMethod(int code) {
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

    @Override
    public int encodingLength() {
        return ENCODING_LENGTH;
    }

    @Override
    public byte[] encoding() {
        return new byte[] { (byte) code };
    }

    public static CompressionMethod createNull() {
        return new CompressionMethod(0);
    }

    public static CompressionMethod parse(ByteBuffer buffer) {
        return new CompressionMethod(buffer.get() & 0xFF);
    }

    private static void check(int code) {
        if (code < 0 || code > 255) {
            throw new IllegalArgumentException();
        }
    }

}
