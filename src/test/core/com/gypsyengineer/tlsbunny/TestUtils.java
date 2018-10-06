package com.gypsyengineer.tlsbunny;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.WhatTheHell;

import java.util.List;

import static org.junit.Assert.fail;

public class TestUtils {

    public interface TestAction {
        void run() throws Exception;
    }

    public static void expectIllegalState(TestAction test) throws Exception {
        try {
            test.run();
            fail("expected an exception");
        } catch (IllegalStateException e) {
            // good
        }
    }

    public static void expectWhatTheHell(TestAction test) throws Exception {
        try {
            test.run();
            fail("expected an exception");
        } catch (WhatTheHell e) {
            // good
        }
    }

    public static void expectUnsupported(TestAction test) throws Exception {
        try {
            test.run();
            fail("expected an exception");
        } catch (UnsupportedOperationException e) {
            // good
        }
    }

    public static ClientHello createClientHello() {
        StructFactory factory = StructFactory.getDefault();
        return factory.createClientHello(
                ProtocolVersion.TLSv13,
                new Random(),
                new byte[32],
                List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                List.of(CompressionMethod.None),
                List.of(factory.createExtension(
                        ExtensionType.supported_versions,
                        new byte[64])));
    }
}
