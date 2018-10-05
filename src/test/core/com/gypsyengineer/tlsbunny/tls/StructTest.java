package com.gypsyengineer.tlsbunny.tls;

import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ProtocolVersionImpl;
import com.gypsyengineer.tlsbunny.utils.WhatTheHell;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class StructTest {

    @Test
    public void cast() {
        Object object = Struct.cast(ProtocolVersion.TLSv13, ProtocolVersionImpl.class);
        assertTrue(object instanceof ProtocolVersion);
        assertEquals(object, ProtocolVersion.TLSv13);

        object = Struct.cast(ProtocolVersion.TLSv13, ProtocolVersion.class);
        assertTrue(object instanceof ProtocolVersion);
        assertEquals(object, ProtocolVersion.TLSv13);

        object = Struct.cast(ProtocolVersion.TLSv13, Struct.class);
        assertTrue(object instanceof ProtocolVersion);
        assertEquals(object, ProtocolVersion.TLSv13);

        try {
            Struct.cast(ProtocolVersion.TLSv13, ContentType.class);
            fail("expected an exception");
        } catch (WhatTheHell e) {
            // good
        }
    }

    @Test
    public void defaultIterationMethods() {
        Struct struct = new TestStruct();

        assertFalse(struct.composite());
        assertEquals(struct.total(), 0);

        try {
            struct.element(0);
            fail("expected an exception");
        } catch (UnsupportedOperationException e) {
            // good
        }

        try {
            struct.element(0, ProtocolVersion.TLSv13);
            fail("expected an exception");
        } catch (UnsupportedOperationException e) {
            // good
        }
    }

    private static class TestStruct implements Struct {

        private final byte[] bytes = new byte[16];

        @Override
        public int encodingLength() {
            return bytes.length;
        }

        @Override
        public byte[] encoding() {
            return bytes;
        }

        @Override
        public Struct copy() {
            return new TestStruct();
        }
    }
}
