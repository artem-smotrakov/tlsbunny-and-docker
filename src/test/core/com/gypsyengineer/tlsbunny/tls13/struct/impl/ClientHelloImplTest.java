package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import org.junit.Test;

import java.util.List;

import static com.gypsyengineer.tlsbunny.TestUtils.createClientHello;
import static com.gypsyengineer.tlsbunny.TestUtils.expectWhatTheHell;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientHelloImplTest {

    @Test
    public void getAndSet() {
        ClientHello hello = createClientHello();

        assertTrue(hello.composite());
        assertEquals(hello.total(), 6);
        assertEquals(hello.getProtocolVersion(), hello.element(0));
        assertEquals(hello.getRandom(), hello.element(1));
        assertEquals(hello.getLegacySessionId(), hello.element(2));
        assertEquals(hello.getCipherSuites(), hello.element(3));
        assertEquals(hello.getLegacyCompressionMethods(), hello.element(4));
        assertEquals(hello.getExtensions(), hello.element(5));

        hello.element(0, ProtocolVersion.TLSv12);
        assertEquals(hello.getProtocolVersion(), hello.element(0));
        assertEquals(ProtocolVersion.TLSv12, hello.element(0));
    }

    @Test
    public void wrongIndex() throws Exception {
        ClientHello hello = createClientHello();

        expectWhatTheHell(() -> hello.element(7));
        expectWhatTheHell(() -> hello.element(-1));
        expectWhatTheHell(() -> hello.element(42, ProtocolVersion.TLSv12));
        expectWhatTheHell(() -> hello.element(-5, ProtocolVersion.TLSv12));
    }

}
