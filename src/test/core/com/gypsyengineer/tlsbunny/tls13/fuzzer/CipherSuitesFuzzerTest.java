package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.utils.Output;
import org.junit.Test;

import java.util.List;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.SimpleVectorFuzzer.simpleVectorFuzzer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
}
