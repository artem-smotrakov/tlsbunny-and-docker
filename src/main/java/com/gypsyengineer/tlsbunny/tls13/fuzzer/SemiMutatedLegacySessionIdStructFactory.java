package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.HexDump.printHexDiff;

public class SemiMutatedLegacySessionIdStructFactory extends StructFactoryWrapper
        implements Fuzzer<Vector<Byte>> {

    public static final Target DEFAULT_TARGET = Target.client_hello;
    public static final Mode DEFAULT_MODE = Mode.semi_mutated_vector;
    public static final String DEFAULT_START_TEST = "0";
    public static final String STATE_DELIMITER = ":";

    private Target target = DEFAULT_TARGET;
    private Mode mode = DEFAULT_MODE;
    private Fuzzer<Vector<Byte>> fuzzer;

    private final Output output;

    public SemiMutatedLegacySessionIdStructFactory(StructFactory factory,
                                                   Output output) {

        super(factory);
        this.output = output;
        initFuzzer(DEFAULT_START_TEST);
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public void setTarget(String target) {
        setTarget(Target.valueOf(target));
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setMode(String mode) {
        setMode(Mode.valueOf(mode));
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
            output.info("fuzz ClientHello");
            Vector<Byte> fuzzedLegacySessionId = fuzz(hello.getLegacySessionId());

            hello = factory.createClientHello(
                    hello.getProtocolVersion(),
                    hello.getRandom(),
                    fuzzedLegacySessionId,
                    hello.getCipherSuites(),
                    hello.getLegacyCompressionMethods(),
                    hello.getExtensions());
        }

        return hello;
    }

    @Override
    public Vector<Byte> fuzz(Vector<Byte> encoding) {
        throw new UnsupportedOperationException("no session id fuzzing for you!");
    }

    @Override
    public String getState() {
        return String.join(STATE_DELIMITER,
                target.toString(), mode.toString(), fuzzer.getState());
    }

    @Override
    public void setStartTest(long test) {
        setState(String.join(STATE_DELIMITER,
                target.toString(), mode.toString(), String.valueOf(test)));
    }

    @Override
    public void setEndTest(long test) {
        fuzzer.setEndTest(test);
    }

    @Override
    public long getTest() {
        return fuzzer.getTest();
    }

    @Override
    public void setState(String state) {
        if (state == null) {
            throw new IllegalArgumentException();
        }

        state = state.toLowerCase().trim();
        if (state.isEmpty()) {
            throw new IllegalArgumentException();
        }

        target = DEFAULT_TARGET;
        mode = DEFAULT_MODE;
        String subState = DEFAULT_START_TEST;

        String[] parts = state.split(STATE_DELIMITER);
        switch (parts.length) {
            case 1:
                target = Target.valueOf(parts[0]);
                break;
            case 2:
                target = Target.valueOf(parts[0]);
                mode = Mode.valueOf(parts[1]);
                break;
            case 3:
                target = Target.valueOf(parts[0]);
                mode = Mode.valueOf(parts[1]);
                subState = parts[2];
                break;
            default:
                throw new IllegalArgumentException();
        }

        initFuzzer(subState);
    }

    @Override
    public boolean canFuzz() {
        return fuzzer.canFuzz();
    }

    @Override
    public void moveOn() {
        fuzzer.moveOn();
    }

    private void initFuzzer(String state) {
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
    }

}
