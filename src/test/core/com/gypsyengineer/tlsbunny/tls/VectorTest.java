package com.gypsyengineer.tlsbunny.tls;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class VectorTest {

    @Test
    public void opaqueTest() throws IOException {
        opaqueTest(1, 0);
        opaqueTest(1, 1);
        opaqueTest(1, 7);
        opaqueTest(1, 16);
        opaqueTest(1, 42);
        opaqueTest(1, 64);
        opaqueTest(1, 100);
        opaqueTest(1, 128);
        opaqueTest(1, 200);
        opaqueTest(1, 255);
        opaqueTest(2, 0);
        opaqueTest(2, 1);
        opaqueTest(2, 7);
        opaqueTest(2, 16);
        opaqueTest(2, 42);
        opaqueTest(2, 64);
        opaqueTest(2, 100);
        opaqueTest(2, 128);
        opaqueTest(2, 200);
        opaqueTest(2, 255);
        opaqueTest(2, 256);
        opaqueTest(2, 1000);
        opaqueTest(2, 22345);
        opaqueTest(2, 65535);
        opaqueTest(3, 0);
        opaqueTest(3, 1);
        opaqueTest(3, 100000);
    }

    @Test
    public void overflow() throws IOException {
        try {
            opaqueTest(1, 256);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }

        try {
            opaqueTest(2, 65536);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    private static void opaqueTest(int lengthBytes, int n) throws IOException {
        Vector one = Vector.wrap(lengthBytes, new byte[n]);
        Vector two = Vector.parseOpaqueVector(ByteBuffer.wrap(one.encoding()), lengthBytes);
        assertEquals(one, two);
        assertEquals(one.lengthBytes(), two.lengthBytes());
        assertArrayEquals(one.encoding(), two.encoding());
        assertArrayEquals(one.bytes(), two.bytes());
        assertEquals(one.toList(), two.toList());
        assertEquals(one.size(), two.size());
        assertEquals(one.isEmpty(), two.isEmpty());
        if (!one.isEmpty()) {
            assertEquals(one.first(), two.first());
        }
    }

}
