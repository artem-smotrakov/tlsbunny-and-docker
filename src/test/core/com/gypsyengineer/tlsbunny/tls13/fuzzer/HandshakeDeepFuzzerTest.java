package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import org.junit.Test;

import java.util.List;

import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.client_hello;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.finished;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HandshakeDeepFuzzerTest {

    @Test
    public void recording() {
        HandshakeDeepFuzzer fuzzer = HandshakeDeepFuzzer.handshakeDeepFuzzer();
        assertTrue(fuzzer.recorded().length == 0);

        // check that recording is not enabled by default
        assertNotNull(createClientHello(fuzzer));
        assertTrue(fuzzer.recorded().length == 0);

        // enable recording
        fuzzer.recording();
        assertTrue(fuzzer.recorded().length == 0);
        assertNotNull(createClientHello(fuzzer));
        assertTrue(fuzzer.recorded().length == 1);
        assertNotNull(createClientHello(fuzzer));
        assertTrue(fuzzer.recorded().length == 2);
        assertNotNull(createFinished(fuzzer));
        assertTrue(fuzzer.recorded().length == 3);
        assertArrayEquals(
                fuzzer.recorded(),
                new HandshakeType[] { client_hello, client_hello, finished });

        // disable recording
        fuzzer.fuzzing();
        assertTrue(fuzzer.recorded().length == 3);
        assertArrayEquals(
                fuzzer.recorded(),
                new HandshakeType[] { client_hello, client_hello, finished});
        assertNotNull(createClientHello(fuzzer));
        assertTrue(fuzzer.recorded().length == 3);
        assertArrayEquals(
                fuzzer.recorded(),
                new HandshakeType[] { client_hello, client_hello, finished });

        // enable recording again
        fuzzer.recording();
        assertTrue(fuzzer.recorded().length == 0);
        assertNotNull(createClientHello(fuzzer));
        assertTrue(fuzzer.recorded().length == 1);
        assertNotNull(createFinished(fuzzer));
        assertTrue(fuzzer.recorded().length == 2);
        assertNotNull(createFinished(fuzzer));
        assertTrue(fuzzer.recorded().length == 3);
        assertArrayEquals(
                fuzzer.recorded(),
                new HandshakeType[] { client_hello, finished, finished });
    }

    private static ClientHello createClientHello(StructFactory factory) {
        return factory.createClientHello(
                ProtocolVersion.TLSv13,
                new Random(),
                new byte[32],
                List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                List.of(CompressionMethod.None),
                List.of());
    }

    private static Finished createFinished(StructFactory factory) {
        return factory.createFinished(new byte[32]);
    }
}
