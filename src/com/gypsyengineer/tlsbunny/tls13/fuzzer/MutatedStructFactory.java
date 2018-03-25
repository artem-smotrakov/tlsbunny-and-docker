package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.ClientHello;
import com.gypsyengineer.tlsbunny.tls13.struct.CompressionMethod;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactoryWrapper;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.Output;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MutatedStructFactory extends StructFactoryWrapper
        implements Fuzzer<byte[]> {

    public enum Mode   { byte_flip, bit_flip }
    public enum Target { tlsplaintext, handshake, client_hello, finished }

    public static final Target DEFAULT_TARGET = Target.tlsplaintext;
    public static final Mode DEFAULT_MODE = Mode.byte_flip;
    public static final String DEFAULT_START_TEST = "0";
    public static final String STATE_DELIMITER = ":";

    private final double minRatio;
    private final double maxRatio;

    private Target target = DEFAULT_TARGET;
    private Mode mode = DEFAULT_MODE;
    private Fuzzer<byte[]> fuzzer;

    private final Output output;

    public MutatedStructFactory(StructFactory factory, Output output,
            double minRatio, double maxRatio) {

        super(factory);
        this.minRatio = minRatio;
        this.maxRatio = maxRatio;
        this.output = output;
        initFuzzer(DEFAULT_START_TEST);
    }

    public void setTarget(String target) {
        this.target = Target.valueOf(target);
    }

    public void setMode(String mode) {
        this.mode = Mode.valueOf(mode);
    }

    @Override
    public TLSPlaintext[] createTLSPlaintexts(
            ContentType type, ProtocolVersion version, byte[] content) {

        TLSPlaintext[] tlsPlaintexts = factory.createTLSPlaintexts(
                type, version, content);

        if (target == Target.tlsplaintext) {
            output.info("fuzz TLSPlaintext");
            try {
                tlsPlaintexts[0] = new MutatedStruct(
                        fuzz(tlsPlaintexts[0].encoding()));
            } catch (IOException e) {
                output.achtung("I couldn't fuzz TLSPlaintext: %s", e.getMessage());
            }
        }

        return tlsPlaintexts;
    }

    @Override
    public Handshake createHandshake(HandshakeType type, byte[] content) {
        Handshake handshake = factory.createHandshake(type, content);

        if (target == Target.handshake) {
            output.info("fuzz Handshake");
            try {
                handshake = new MutatedStruct(fuzz(handshake.encoding()));
            } catch (IOException e) {
                output.achtung("I couldn't fuzz Handshake: %s", e.getMessage());
            }
        }

        return handshake;
    }

    @Override
    public ClientHello createClientHello(
            ProtocolVersion legacy_version,
            Random random,
            byte[] legacy_session_id,
            List<CipherSuite> cipher_suites,
            List<CompressionMethod> legacy_compression_methods,
            List<Extension> extensions) {

        ClientHello clientHello = factory.createClientHello(
                legacy_version, random, legacy_session_id, cipher_suites,
                legacy_compression_methods, extensions);

        if (target == Target.client_hello) {
            output.info("fuzz ClientHello");
            try {
                byte[] fuzzed = fuzz(clientHello.encoding());
                clientHello = new MutatedStruct(
                        fuzzed.length, fuzzed, HandshakeType.client_hello);
            } catch (IOException e) {
                output.achtung("I couldn't fuzz ClientHello: %s", e.getMessage());
            }
        }

        return clientHello;
    }

    @Override
    public Finished createFinished(byte[] verify_data) {
        Finished finished = factory.createFinished(verify_data);

        if (target == Target.finished) {
            output.info("fuzz Finished");
            try {
                byte[] fuzzed = fuzz(finished.encoding());
                finished = new MutatedStruct(
                        fuzzed.length, fuzzed, HandshakeType.finished);
            } catch (IOException e) {
                output.achtung("I couldn't fuzz ClientHello: %s", e.getMessage());
            }
        }

        return finished;
    }

    @Override
    public byte[] fuzz(byte[] encoding) {
        byte[] fuzzed = fuzzer.fuzz(encoding);
        if (Arrays.equals(encoding, fuzzed)) {
            output.achtung("nothing actually fuzzed");
        }

        return fuzzed;
    }

    @Override
    public String getState() {
        return String.join(STATE_DELIMITER,
                target.toString(), mode.toString(), fuzzer.getState());
    }

    @Override
    public void setTest(long test) {
        setState(String.join(STATE_DELIMITER,
                target.toString(), mode.toString(), String.valueOf(test)));
    }

    @Override
    public long getTest() {
        return fuzzer.getTest();
    }

    @Override
    public void setLimit(long limit) {
        fuzzer.setLimit(limit);
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
         // fuzz all content of a message by default
        int start = -1;
        int end = -1;

        switch (target) {
            case tlsplaintext:
                // in case of TLSPlaintext we don't fuzz the content of the message
                // but only content type, protocol version and lenght fields
                end = ContentType.ENCODING_LENGTH
                        + ProtocolVersion.ENCODING_LENGTH
                        + UInt16.ENCODING_LENGTH;
                break;
            case handshake:
                // in case of Handshake message we don't fuzz the content of the message
                // but only handshake type and length fields
                end = HandshakeType.ENCODING_LENGTH + UInt24.ENCODING_LENGTH;
                break;
        }

        switch (mode) {
            case byte_flip:
                fuzzer = new ByteFlipFuzzer(minRatio, maxRatio, start, end);
                break;
            case bit_flip:
                fuzzer = new BitFlipFuzzer(minRatio, maxRatio, start, end);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        fuzzer.setState(state);
    }

    public static String[] getAvailableTargets() {
        Target[] values = Target.values();
        String[] targets = new String[values.length];
        for (int i=0; i<values.length; i++) {
            targets[i] = values[i].name();
        }

        return targets;
    }

}
