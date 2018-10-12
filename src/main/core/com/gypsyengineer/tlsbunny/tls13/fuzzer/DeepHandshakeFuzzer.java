package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.ArrayList;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class DeepHandshakeFuzzer extends FuzzyStructFactory<HandshakeMessage> {

    private static final int rounds_per_path = 10;

    private Mode mode;

    private final List<Holder> recorded = new ArrayList<>();
    private int currentHolder = 0;
    private int round = 0;

    public static DeepHandshakeFuzzer deepHandshakeFuzzer() {
        return new DeepHandshakeFuzzer(StructFactory.getDefault(), new Output());
    }

    private DeepHandshakeFuzzer(StructFactory factory, Output output) {
        super(factory, output);
    }

    public synchronized HandshakeType[] targeted() {
        HandshakeType[] targeted = new HandshakeType[recorded.size()];

        int i = 0;
        for (Holder holder : recorded) {
            targeted[i++] = holder.message.type();
        }

        return targeted;
    }

    private boolean shouldFuzz(HandshakeMessage message) {
        if (mode != Mode.fuzzing || recorded.isEmpty()) {
            return false;
        }

        return currentHolder().match(message);
    }

    private Holder currentHolder() {
        return recorded.get(currentHolder);
    }

    // switch to recording mode
    public synchronized DeepHandshakeFuzzer recording() {
        mode = Mode.recording;
        recorded.clear();
        return this;
    }

    // switch to fuzzing mode
    public synchronized DeepHandshakeFuzzer fuzzing() {
        mode = Mode.fuzzing;
        return this;
    }

    // override methods from FuzzyStructFactory
    // setting targets are not currently supported

    @Override
    public synchronized FuzzyStructFactory target(Target target) {
        throw new UnsupportedOperationException("no targets for you");
    }

    @Override
    public synchronized FuzzyStructFactory target(String target) {
        throw new UnsupportedOperationException("no targets for you");
    }

    @Override
    public synchronized Target target() {
        throw new UnsupportedOperationException("no targets for you");
    }

    // override methods for Fuzzer

    @Override
    public synchronized long currentTest() {
        return super.currentTest();
    }

    @Override
    public synchronized void currentTest(long test) {
        super.currentTest(test);
    }

    @Override
    public synchronized boolean canFuzz() {
        return super.canFuzz();
    }

    @Override
    public synchronized void moveOn() {
        super.moveOn();
    }

    @Override
    public synchronized HandshakeMessage fuzz(HandshakeMessage message) {
        if (mode != Mode.fuzzing) {
            throw whatTheHell("can't start fuzzing in mode '%s'", mode);
        }

        if (recorded.isEmpty()) {
            throw whatTheHell("can't start fuzzing since no messages were targeted!");
        }

        if (!currentHolder().match(message)) {
            return message;
        }

        // TODO: do fuzzing here

        boolean finished = incrementRound();
        if (finished) {
            nextTarget();
        }

        return message;
    }

    List<Holder> recorded() {
        return recorded;
    }

    private boolean incrementRound() {
        if (round == rounds_per_path - 1) {
            round = 0;
            return true;
        }

        round++;
        return false;
    }

    private void nextTarget() {
        currentHolder++;
        currentHolder %= recorded.size();
    }

    private DeepHandshakeFuzzer record(HandshakeMessage message) {
        recorded.add(new Holder(message));
        return this;
    }

    private MessageAction with(HandshakeMessage message) {
        return new MessageAction(this, message);
    }

    private <T> T handle(HandshakeMessage message) {
        return with(message)
                .record()
                .fuzz()
                .get();
    }

    // override only methods for creating Handshake messages

    @Override
    public synchronized Certificate createCertificate(Vector<Byte> certificate_request_context,
                                                      Vector<CertificateEntry> certificate_list) {

        return handle(super.createCertificate(certificate_request_context, certificate_list));
    }

    @Override
    public synchronized Certificate createCertificate(byte[] certificate_request_context,
                                                      CertificateEntry... certificate_list) {

        return handle(super.createCertificate(certificate_request_context, certificate_list));
    }

    @Override
    public synchronized CertificateRequest createCertificateRequest(
            byte[] certificate_request_context, Vector<Extension> extensions) {

        return handle(super.createCertificateRequest(certificate_request_context, extensions));
    }

    @Override
    public synchronized CertificateVerify createCertificateVerify(SignatureScheme algorithm,
                                                                  byte[] signature) {

        return handle(super.createCertificateVerify(algorithm, signature));
    }

    @Override
    public synchronized ClientHello createClientHello(ProtocolVersion legacy_version,
                                                      Random random,
                                                      Vector<Byte> legacy_session_id,
                                                      Vector<CipherSuite> cipher_suites,
                                                      Vector<CompressionMethod> legacy_compression_methods,
                                                      Vector<Extension> extensions) {

        return handle(super.createClientHello(legacy_version, random,
                legacy_session_id, cipher_suites, legacy_compression_methods, extensions));
    }

    @Override
    public synchronized EncryptedExtensions createEncryptedExtensions(Extension... extensions) {
        return handle(super.createEncryptedExtensions(extensions));
    }

    @Override
    public synchronized EndOfEarlyData createEndOfEarlyData() {
        return handle(super.createEndOfEarlyData());
    }

    @Override
    public synchronized Finished createFinished(byte[] verify_data) {
        return handle(super.createFinished(verify_data));
    }

    @Override
    public synchronized HelloRetryRequest createHelloRetryRequest() {
        return handle(super.createHelloRetryRequest());
    }

    @Override
    public synchronized ServerHello createServerHello(ProtocolVersion version,
                                                      Random random,
                                                      Vector<Byte> legacy_session_id_echo,
                                                      CipherSuite cipher_suite,
                                                      CompressionMethod legacy_compression_method,
                                                      Vector<Extension> extensions) {

        return handle(super.createServerHello(version, random, legacy_session_id_echo,
                cipher_suite, legacy_compression_method, extensions));
    }

    private enum Mode { recording, fuzzing }

    private static class MessageAction {

        private final DeepHandshakeFuzzer fuzzer;
        private final Class clazz;
        private HandshakeMessage message;

        MessageAction(DeepHandshakeFuzzer fuzzer, HandshakeMessage message) {
            this.fuzzer = fuzzer;
            this.message = message;
            this.clazz = message.getClass();
        }

        public MessageAction fuzz() {
            if (fuzzer.shouldFuzz(message)) {
                message = fuzzer.fuzz(message);
            }
            return this;
        }

        public MessageAction record() {
            if (fuzzer.mode == Mode.recording) {
                fuzzer.record(message);
            }
            return this;
        }

        public <T> T get() {
            if (!clazz.isAssignableFrom(message.getClass())) {
                throw whatTheHell("expected %s but received %s",
                        clazz.getSimpleName(), message.getClass().getSimpleName());
            }
            return (T) message;
        }

    }

    static class Path {

        private final List<Integer> indexes;

        private Path() {
            this.indexes = new ArrayList<>();
        }

        Path copy() {
            Path clone = new Path();
            for (int index : indexes) {
                clone.indexes.add(index);
            }
            return clone;
        }

        Path add(int index) {
            indexes.add(index);
            return this;
        }

        Integer[] indexes() {
            return indexes.toArray(new Integer[indexes.size()]);
        }
    }

    private static Path[] browse(HandshakeMessage message) {
        List<Path> paths = new ArrayList<>();
        browse(message, new Path(), paths);
        return paths.toArray(new Path[paths.size()]);
    }

    private static void browse(Struct struct, Path path, List<Path> paths) {
        paths.add(path);
        for (int index = 0; index < struct.total(); index++) {
             browse(struct.element(index), path.copy().add(index), paths);
        }
    }

    static class Holder {

        private final HandshakeMessage message;
        private final Path[] paths;

        Holder(HandshakeMessage message) {
            this.message = message;
            this.paths = browse(message);
        }

        boolean match(HandshakeMessage message) {
            return this.message.type().equals(message.type());
        }

        HandshakeMessage message() {
            return message;
        }

        Path[] paths() {
            return paths;
        }
    }
}
