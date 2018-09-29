package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.fuzzer.FuzzedVector;
import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.ClientHello;
import com.gypsyengineer.tlsbunny.tls13.struct.CompressionMethod;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.utils.Output;
import org.junit.Test;

import java.util.List;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.SimpleVectorFuzzer.simpleVectorFuzzer;
import static org.junit.Assert.*;

public class CipherSuitesFuzzerTest {

    @Test
    public void iterate() {
        try (Output output = new Output()) {
            CipherSuitesFuzzer fuzzer = CipherSuitesFuzzer.cipherSuitesFuzzer();
            fuzzer.fuzzer(simpleVectorFuzzer());

            fuzzer.target(Target.client_hello);
            assertEquals(Target.client_hello, fuzzer.target());

            fuzzer.set(output);
            assertEquals(output, fuzzer.output());

            assertTrue(fuzzer.canFuzz());

            int expectedState = 0;

            Vector<CipherSuite> cipherSuites = Vector.wrap(2, List.of(
                    CipherSuite.TLS_AES_128_GCM_SHA256,
                    CipherSuite.TLS_AES_128_CCM_8_SHA256,
                    CipherSuite.TLS_CHACHA20_POLY1305_SHA256));

            int m = 10;
            for (int i = 0; i < m; i++) {
                assertTrue(fuzzer.canFuzz());
                assertEquals(expectedState, fuzzer.currentTest());

                Vector<CipherSuite> fuzzed = fuzzer.fuzz(cipherSuites);
                assertNotEquals(fuzzed, cipherSuites);
                assertEquals(fuzzed, fuzzer.fuzz(cipherSuites));

                fuzzer.moveOn();
                expectedState++;
            }
        }
    }

    @Test
    public void consistency() {
        try (Output output = new Output()) {
            CipherSuitesFuzzer one = CipherSuitesFuzzer.cipherSuitesFuzzer();
            one.fuzzer(simpleVectorFuzzer());
            one.target(Target.client_hello);
            one.set(output);
            assertTrue(one.canFuzz());


            CipherSuitesFuzzer two = CipherSuitesFuzzer.cipherSuitesFuzzer();
            two.fuzzer(simpleVectorFuzzer());
            two.target(Target.client_hello);
            two.set(output);
            assertTrue(two.canFuzz());

            Vector<CipherSuite> cipherSuites = Vector.wrap(2, List.of(
                    CipherSuite.TLS_AES_128_GCM_SHA256,
                    CipherSuite.TLS_AES_128_CCM_8_SHA256,
                    CipherSuite.TLS_CHACHA20_POLY1305_SHA256));

            while (one.canFuzz()) {
                Vector fuzzedOne = one.fuzz(cipherSuites);
                Vector fuzzedTwo = two.fuzz(cipherSuites);
                assertEquals(fuzzedOne, fuzzedTwo);
                assertNotEquals(fuzzedOne, cipherSuites);
                assertNotEquals(fuzzedTwo, cipherSuites);

                one.moveOn();
                two.moveOn();
            }

            assertFalse(one.canFuzz());
            assertFalse(two.canFuzz());
        }
    }

    @Test
    public void clientHello() {
        try (Output output = new Output()) {
            CipherSuitesFuzzer fuzzer = CipherSuitesFuzzer.cipherSuitesFuzzer();
            fuzzer.fuzzer(simpleVectorFuzzer());
            fuzzer.set(output);

            assertEquals(Target.client_hello, fuzzer.target());

            fuzzer.moveOn();
            ClientHello clientHelloOne = fuzzer.createClientHello(
                    ProtocolVersion.TLSv13,
                    new Random(),
                    new byte[8],
                    List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                    List.of(CompressionMethod.None),
                    List.of());
            assertTrue(clientHelloOne.getCipherSuites() instanceof FuzzedVector);

            fuzzer.moveOn();
            ClientHello clientHelloTwo = fuzzer.createClientHello(
                    ProtocolVersion.TLSv13,
                    new Random(),
                    new byte[8],
                    List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                    List.of(CompressionMethod.None),
                    List.of());
            assertTrue(clientHelloTwo.getCipherSuites() instanceof FuzzedVector);

            assertNotEquals(clientHelloOne, clientHelloTwo);

            fuzzer.target(Target.tls_plaintext);
            assertEquals(Target.tls_plaintext, fuzzer.target());

            fuzzer.moveOn();
            clientHelloOne = fuzzer.createClientHello(
                    ProtocolVersion.TLSv13,
                    new Random(),
                    new byte[8],
                    List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                    List.of(CompressionMethod.None),
                    List.of());
            assertFalse(clientHelloOne.getCipherSuites() instanceof FuzzedVector);

            fuzzer.moveOn();
            clientHelloTwo = fuzzer.createClientHello(
                    ProtocolVersion.TLSv13,
                    new Random(),
                    new byte[8],
                    List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                    List.of(CompressionMethod.None),
                    List.of());
            assertFalse(clientHelloTwo.getCipherSuites() instanceof FuzzedVector);

            assertEquals(clientHelloOne, clientHelloTwo);

            fuzzer.target(Target.client_hello);
            assertEquals(Target.client_hello, fuzzer.target());

            fuzzer.moveOn();
            clientHelloOne = fuzzer.createClientHello(
                    ProtocolVersion.TLSv13,
                    new Random(),
                    new byte[8],
                    List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                    List.of(CompressionMethod.None),
                    List.of());
            assertTrue(clientHelloOne.getCipherSuites() instanceof FuzzedVector);

            fuzzer.moveOn();
            clientHelloTwo = fuzzer.createClientHello(
                    ProtocolVersion.TLSv13,
                    new Random(),
                    new byte[8],
                    List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                    List.of(CompressionMethod.None),
                    List.of());
            assertTrue(clientHelloTwo.getCipherSuites() instanceof FuzzedVector);

            assertNotEquals(clientHelloOne, clientHelloTwo);
        }
    }
}
