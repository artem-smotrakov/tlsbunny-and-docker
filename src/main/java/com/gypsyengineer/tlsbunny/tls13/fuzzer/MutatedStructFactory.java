package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.HexDump.printHexDiff;

public class MutatedStructFactory extends FuzzyStructFactory<byte[]> {

    public static final Target DEFAULT_TARGET = Target.tls_plaintext;
    public static final Mode DEFAULT_MODE = Mode.byte_flip;

    private final double minRatio;
    private final double maxRatio;

    public MutatedStructFactory(StructFactory factory, Output output,
            double minRatio, double maxRatio) {

        super(factory, output);
        mode(DEFAULT_MODE);
        target(DEFAULT_TARGET);
        this.minRatio = minRatio;
        this.maxRatio = maxRatio;
        initFuzzer(DEFAULT_START_TEST);
    }

    @Override
    public TLSPlaintext[] createTLSPlaintexts(
            ContentType type, ProtocolVersion version, byte[] content) {

        TLSPlaintext[] tlsPlaintexts = factory.createTLSPlaintexts(
                type, version, content);

        if (target == Target.tls_plaintext) {
            int index = 0;
            try {
                byte[] encoding = tlsPlaintexts[index].encoding();
                output.info("fuzz TLSPlaintext[%d] (total is %d)",
                        index, tlsPlaintexts.length);
                tlsPlaintexts[index] = new MutatedStruct(fuzz(encoding));
            } catch (IOException e) {
                output.achtung("I couldn't fuzz TLSPlaintext[%d]: %s",
                        e.getMessage(), index);
            }
        }

        return tlsPlaintexts;
    }

    @Override
    public TLSPlaintext createTLSPlaintext(
            ContentType type, ProtocolVersion version, byte[] content) {

        TLSPlaintext tlsPlaintext = factory.createTLSPlaintext(
                type, version, content);

        if (target == Target.tls_plaintext) {
            output.info("fuzz TLSPlaintext");
            try {
                tlsPlaintext = new MutatedStruct(fuzz(tlsPlaintext.encoding()));
            } catch (IOException e) {
                output.achtung("I couldn't fuzz TLSPlaintext: %s", e.getMessage());
            }
        }

        return tlsPlaintext;
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
                output.achtung("I couldn't fuzz Finished: %s", e.getMessage());
            }
        }

        return finished;
    }

    @Override
    public Certificate createCertificate(
            byte[] certificate_request_context, CertificateEntry... certificate_list) {

        Certificate certificate = factory.createCertificate(
                certificate_request_context, certificate_list);

        if (target == Target.certificate) {
            output.info("fuzz Certificate");
            try {
                byte[] fuzzed = fuzz(certificate.encoding());
                certificate = new MutatedStruct(
                        fuzzed.length, fuzzed, HandshakeType.certificate);
            } catch (IOException e) {
                output.achtung("I couldn't fuzz Certificate: %s", e.getMessage());
            }
        }

        return certificate;
    }

    @Override
    public CertificateVerify createCertificateVerify(
            SignatureScheme algorithm, byte[] signature) {

        CertificateVerify certificateVerify = factory.createCertificateVerify(
                algorithm, signature);

        if (target == Target.certificate_verify) {
            output.info("fuzz CertificateVerify");
            try {
                byte[] fuzzed = fuzz(certificateVerify.encoding());
                certificateVerify = new MutatedStruct(
                        fuzzed.length, fuzzed, HandshakeType.certificate);
            } catch (IOException e) {
                output.achtung("I couldn't fuzz CertificateVerify: %s", e.getMessage());
            }
        }

        return certificateVerify;
    }

    @Override
    public ChangeCipherSpec createChangeCipherSpec(int value) {
        ChangeCipherSpec ccs = factory.createChangeCipherSpec(value);

        if (target == Target.ccs) {
            output.info("fuzz ChangeCipherSpec");
            try {
                byte[] fuzzed = fuzz(ccs.encoding());
                ccs = new MutatedStruct(fuzzed.length, fuzzed);
            } catch (IOException e) {
                output.achtung("I couldn't fuzz ChangeCipherSpec: %s", e.getMessage());
            }
        }

        return ccs;
    }

    @Override
    public byte[] fuzz(byte[] encoding) {
        byte[] fuzzed = fuzzer.fuzz(encoding);

        output.info("%s (original): %n", target);
        output.increaseIndent();
        output.info("%s%n", printHexDiff(encoding, fuzzed));
        output.decreaseIndent();
        output.info("%s (fuzzed): %n", target);
        output.increaseIndent();
        output.info("%s%n", printHexDiff(fuzzed, encoding));
        output.decreaseIndent();

        if (Arrays.equals(encoding, fuzzed)) {
            output.achtung("nothing actually fuzzed");
        }

        return fuzzed;
    }

    void initFuzzer(String state) {
        // fuzz all content of a message by default
        int start = -1;
        int end = -1;

        switch (target) {
            case tls_plaintext:
                // in case of TLSPlaintext we don't fuzz the content of the message
                // but only content type, protocol version and length fields
                start = 0;
                end = ContentType.ENCODING_LENGTH
                        + ProtocolVersion.ENCODING_LENGTH
                        + UInt16.ENCODING_LENGTH - 1;
                break;
            case handshake:
                // in case of Handshake message we don't fuzz the content of the message
                // but only handshake type and length fields
                start = 0;
                end = HandshakeType.ENCODING_LENGTH + UInt24.ENCODING_LENGTH - 1;
                break;
            case ccs:
            case client_hello:
            case certificate:
            case certificate_verify:
            case finished:
                // okay, we can do that
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "what the hell? target '%s' not supported", target));
        }

        switch (mode) {
            case byte_flip:
                fuzzer = new ByteFlipFuzzer(minRatio, maxRatio, start, end);
                break;
            case bit_flip:
                fuzzer = new BitFlipFuzzer(minRatio, maxRatio, start, end);
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "what the hell? mode '%s' not supported", mode));
        }

        fuzzer.setState(state);
    }

}
