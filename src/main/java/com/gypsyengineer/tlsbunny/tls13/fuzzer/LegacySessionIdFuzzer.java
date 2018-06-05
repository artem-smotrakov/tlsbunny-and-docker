package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.HexDump.printHexDiff;

public class LegacySessionIdFuzzer extends FuzzyStructFactory<Vector<Byte>> {

    public static final Target DEFAULT_TARGET = Target.client_hello;

    public static LegacySessionIdFuzzer newMutatedLegacySessionIdStructFactory() {
        return new LegacySessionIdFuzzer();
    }

    public LegacySessionIdFuzzer() {
        this(StructFactory.getDefault(), new Output());
    }

    public LegacySessionIdFuzzer(StructFactory factory,
                                 Output output) {
        super(factory, output);
        target(DEFAULT_TARGET);
    }

    @Override
    synchronized public ClientHello createClientHello(
            ProtocolVersion legacy_version,
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
            output.info("fuzz legacy session ID in ClientHello");
            hello = factory.createClientHello(
                    hello.getProtocolVersion(),
                    hello.getRandom(),
                    fuzz(hello.getLegacySessionId()),
                    hello.getCipherSuites(),
                    hello.getLegacyCompressionMethods(),
                    hello.getExtensions());
        }

        return hello;
    }

    @Override
    synchronized public Vector<Byte> fuzz(Vector<Byte> sessionId) {
        Vector<Byte> fuzzedSessionId = fuzzer.fuzz(sessionId);

        try {
            byte[] encoding = sessionId.encoding();
            byte[] fuzzed = fuzzedSessionId.encoding();
            output.info("legacy session ID in %s (original): %n", target);
            output.increaseIndent();
            output.info("%s%n", printHexDiff(encoding, fuzzed));
            output.decreaseIndent();
            output.info("legacy session ID in %s (fuzzed): %n", target);
            output.increaseIndent();
            output.info("%s%n", printHexDiff(fuzzed, encoding));
            output.decreaseIndent();

            if (Vector.equals(fuzzedSessionId, sessionId)) {
                output.achtung("nothing actually fuzzed");
            }
        } catch (IOException e) {
            output.achtung("what the hell?", e);
        }

        return fuzzedSessionId;
    }

}
