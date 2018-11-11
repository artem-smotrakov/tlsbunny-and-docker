package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.client_hello;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Target.server_hello;
import static com.gypsyengineer.tlsbunny.utils.HexDump.printHexDiff;

public class CipherSuitesFuzzer extends FuzzyStructFactory<Vector<CipherSuite>> {

    public static CipherSuitesFuzzer cipherSuitesFuzzer() {
        return new CipherSuitesFuzzer();
    }

    public CipherSuitesFuzzer() {
        this(StructFactory.getDefault(), new Output());
    }

    public CipherSuitesFuzzer(StructFactory factory, Output output) {
        super(factory, output);
        targets(client_hello, server_hello);
    }

    @Override
    synchronized public ClientHello createClientHello(ProtocolVersion legacy_version,
                                                      Random random,
                                                      Vector<Byte> legacy_session_id,
                                                      Vector<CipherSuite> cipher_suites,
                                                      Vector<CompressionMethod> legacy_compression_methods,
                                                      Vector<Extension> extensions) {

        if (targeted(client_hello)) {
            output.info("fuzz cipher suites in ClientHello");
            cipher_suites = fuzz(cipher_suites);
        }

        return factory.createClientHello(
                legacy_version,
                random,
                legacy_session_id,
                cipher_suites,
                legacy_compression_methods,
                extensions);
    }

    @Override
    synchronized public Vector<CipherSuite> fuzz(Vector<CipherSuite> cipherSuites) {
        Vector<CipherSuite> fuzzedCipherSuites = fuzzer.fuzz(cipherSuites);

        try {
            byte[] encoding = cipherSuites.encoding();
            byte[] fuzzed = fuzzedCipherSuites.encoding();
            output.info("cipher suites (original): %n");
            output.increaseIndent();
            output.info("%s%n", printHexDiff(encoding, fuzzed));
            output.decreaseIndent();
            output.info("cipher suites (fuzzed): %n");
            output.increaseIndent();
            output.info("%s%n", printHexDiff(fuzzed, encoding));
            output.decreaseIndent();

            if (Vector.equals(fuzzedCipherSuites, cipherSuites)) {
                output.achtung("nothing actually fuzzed");
            }
        } catch (IOException e) {
            output.achtung("what the hell?", e);
        }

        return fuzzedCipherSuites;
    }

}
