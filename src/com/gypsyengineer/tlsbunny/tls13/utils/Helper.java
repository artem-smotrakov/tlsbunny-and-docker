package com.gypsyengineer.tlsbunny.tls13.utils;

import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Helper {

    public static ByteBuffer store(TLSPlaintext[] tlsPlaintexts) throws IOException {
        int length = 0;
        for (TLSPlaintext tlsPlaintext : tlsPlaintexts) {
            length += tlsPlaintext.encodingLength();
        }

        ByteBuffer buffer = ByteBuffer.allocate(length);
        for (TLSPlaintext tlsPlaintext : tlsPlaintexts) {
            buffer.put(tlsPlaintext.encoding());
        }

        buffer.position(0);

        return buffer;
    }
 }
