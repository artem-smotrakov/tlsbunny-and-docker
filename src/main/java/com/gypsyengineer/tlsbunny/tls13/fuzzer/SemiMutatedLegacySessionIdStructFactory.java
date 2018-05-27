package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.util.List;

public class SemiMutatedLegacySessionIdStructFactory extends FuzzyStructFactory<Vector<Byte>> {

    public static final Target DEFAULT_TARGET = Target.client_hello;
    public static final Mode DEFAULT_MODE = Mode.semi_mutated_vector;

    public SemiMutatedLegacySessionIdStructFactory(StructFactory factory,
                                                   Output output) {
        super(factory, output);
        mode(DEFAULT_MODE);
        target(DEFAULT_TARGET);
        initFuzzer(DEFAULT_START_TEST);
    }

    @Override
    public ClientHello createClientHello(
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
    public Vector<Byte> fuzz(Vector<Byte> legacySessionId) {
        Vector<Byte> fuzzed = fuzzer.fuzz(legacySessionId);

        try {
            if (Vector.equals(fuzzed, legacySessionId)) {
                output.achtung("nothing actually fuzzed");
            }
        } catch (IOException e) {
            output.achtung("what the hell?", e);
        }

        return fuzzed;
    }

    @Override
    public void setOutput(Output output) {
        this.output = output;
    }

    @Override
    public Output getOutput() {
        return output;
    }

    void initFuzzer(String state) {
        switch (target) {
            case client_hello:
                // okay, we support it
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "what the hell? target '%s' not supported", target));
        }

        switch (mode) {
            case semi_mutated_vector:
                fuzzer = new LegacySessionIdFuzzer();
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "what the hell? mode '%s' not supported", mode));
        }

        fuzzer.setState(state);
        fuzzer.setOutput(output);
    }

}
