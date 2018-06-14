package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.HexDump.printHexDiff;

public class ExtensionVectorFuzzer extends FuzzyStructFactory<Vector<Extension>> {

    public static final Target DEFAULT_TARGET = Target.client_hello;

    public static ExtensionVectorFuzzer newExtensionVectorFuzzer() {
        return new ExtensionVectorFuzzer();
    }

    public ExtensionVectorFuzzer() {
        this(StructFactory.getDefault(), new Output());
    }

    public ExtensionVectorFuzzer(StructFactory factory,
                                 Output output) {
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
            output.info("fuzz extension vector in ClientHello");
            hello = factory.createClientHello(
                    hello.getProtocolVersion(),
                    hello.getRandom(),
                    hello.getLegacySessionId(),
                    hello.getCipherSuites(),
                    hello.getLegacyCompressionMethods(),
                    fuzz(hello.getExtensions()));
        }

        return hello;
    }

    @Override
    synchronized public ServerHello createServerHello(ProtocolVersion version,
                                                      Random random,
                                                      byte[] legacy_session_id_echo,
                                                      CipherSuite cipher_suite,
                                                      CompressionMethod legacy_compression_method,
                                                      List<Extension> extensions) {

        ServerHello hello = factory.createServerHello(
                version,
                random,
                legacy_session_id_echo,
                cipher_suite,
                legacy_compression_method,
                extensions);

        if (target == Target.server_hello) {
            output.info("fuzz extensions in ServerHello");
            hello = factory.createServerHello(
                    hello.getProtocolVersion(),
                    hello.getRandom(),
                    hello.getLegacySessionIdEcho(),
                    hello.getCipherSuite(),
                    hello.getLegacyCompressionMethod(),
                    fuzz(hello.getExtensions()));
        }

        return hello;
    }

    @Override
    synchronized public Vector<Extension> fuzz(Vector<Extension> extensions) {
        Vector<Extension> fuzzedExtensions = fuzzer.fuzz(extensions);

        try {
            byte[] encoding = extensions.encoding();
            byte[] fuzzed = fuzzedExtensions.encoding();
            output.info("extensions in %s (original): %n", target);
            output.increaseIndent();
            output.info("%s%n", printHexDiff(encoding, fuzzed));
            output.decreaseIndent();
            output.info("extensions in %s (fuzzed): %n", target);
            output.increaseIndent();
            output.info("%s%n", printHexDiff(fuzzed, encoding));
            output.decreaseIndent();

            if (Vector.equals(fuzzedExtensions, extensions)) {
                output.achtung("nothing actually fuzzed");
            }
        } catch (IOException e) {
            output.achtung("what the hell?", e);
        }

        return fuzzedExtensions;
    }

}
