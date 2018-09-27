package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.HexDump.printHexDiff;

public class CipherSuitesFuzzer extends FuzzyStructFactory<Vector<CipherSuite>> {

    public static final Target DEFAULT_TARGET = Target.client_hello;

    public static CipherSuitesFuzzer cipherSuitesFuzzer() {
        return new CipherSuitesFuzzer();
    }

    public CipherSuitesFuzzer() {
        this(StructFactory.getDefault(), new Output());
    }

    public CipherSuitesFuzzer(StructFactory factory, Output output) {
        super(factory, output);
        target(DEFAULT_TARGET);
    }

    @Override
    synchronized public ClientHello createClientHello(ProtocolVersion legacy_version,
                                                      Random random,
                                                      byte[] legacy_session_id,
                                                      List<CipherSuite> cipher_suites,
                                                      List<CompressionMethod> legacy_compression_methods,
                                                      List<Extension> extensions) {

        ClientHello hello = factory.createClientHello(
                legacy_version,
                random,
                legacy_session_id,
                cipher_suites,
                legacy_compression_methods,
                extensions);

        if (target == Target.client_hello) {
            output.info("fuzz cipher suites in ClientHello");
            hello = factory.createClientHello(
                    hello.getProtocolVersion(),
                    hello.getRandom(),
                    hello.getLegacySessionId(),
                    fuzz(hello.getCipherSuites()),
                    hello.getLegacyCompressionMethods(),
                    hello.getExtensions());
        }

        return hello;
    }

    @Override
    synchronized public Vector<CipherSuite> fuzz(Vector<CipherSuite> cipherSuites) {
        Vector<CipherSuite> fuzzedCipherSuites = fuzzer.fuzz(cipherSuites);

        try {
            byte[] encoding = cipherSuites.encoding();
            byte[] fuzzed = fuzzedCipherSuites.encoding();
            output.info("cipher suites in %s (original): %n", target);
            output.increaseIndent();
            output.info("%s%n", printHexDiff(encoding, fuzzed));
            output.decreaseIndent();
            output.info("cipher suites in %s (fuzzed): %n", target);
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
