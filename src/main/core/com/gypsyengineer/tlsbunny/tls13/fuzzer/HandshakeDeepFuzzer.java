package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.ArrayList;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class HandshakeDeepFuzzer extends FuzzyStructFactory<HandshakeMessage> {

    private Mode mode;
    private List<HandshakeType> recorded = new ArrayList<>();

    public static HandshakeDeepFuzzer handshakeDeepFuzzer() {
        return new HandshakeDeepFuzzer(StructFactory.getDefault(), new Output());
    }

    private HandshakeDeepFuzzer(StructFactory factory, Output output) {
        super(factory, output);
    }

    public HandshakeType[] recorded() {
        return recorded.toArray(new HandshakeType[recorded.size()]);
    }

    // switch to recording mode
    public HandshakeDeepFuzzer recording() {
        mode = Mode.recording;
        recorded = new ArrayList<>();
        return this;
    }

    // switch to fuzzing mode
    public HandshakeDeepFuzzer fuzzing() {
        mode = Mode.fuzzing;
        return this;
    }

    @Override
    public HandshakeMessage fuzz(HandshakeMessage message) {
        if (mode != Mode.fuzzing) {
            throw whatTheHell("can't start fuzzing in mode '%s'", mode);
        }

        // TODO: fuzz
        return message;
    }

    private HandshakeDeepFuzzer record(HandshakeMessage message) {
        recorded.add(message.type());
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
    public Certificate createCertificate(Vector<Byte> certificate_request_context,
                                         Vector<CertificateEntry> certificate_list) {

        return handle(super.createCertificate(certificate_request_context, certificate_list));
    }

    @Override
    public Certificate createCertificate(byte[] certificate_request_context,
                                         CertificateEntry... certificate_list) {

        return handle(super.createCertificate(certificate_request_context, certificate_list));
    }

    @Override
    public CertificateRequest createCertificateRequest(byte[] certificate_request_context,
                                                       Vector<Extension> extensions) {

        return handle(super.createCertificateRequest(certificate_request_context, extensions));
    }

    @Override
    public CertificateVerify createCertificateVerify(SignatureScheme algorithm,
                                                     byte[] signature) {

        return handle(super.createCertificateVerify(algorithm, signature));
    }

    @Override
    public ClientHello createClientHello(ProtocolVersion legacy_version,
                                         Random random,
                                         Vector<Byte> legacy_session_id,
                                         Vector<CipherSuite> cipher_suites,
                                         Vector<CompressionMethod> legacy_compression_methods,
                                         Vector<Extension> extensions) {

        return handle(super.createClientHello(legacy_version, random,
                legacy_session_id, cipher_suites, legacy_compression_methods, extensions));
    }

    @Override
    public EncryptedExtensions createEncryptedExtensions(Extension... extensions) {
        return handle(super.createEncryptedExtensions(extensions));
    }

    @Override
    public EndOfEarlyData createEndOfEarlyData() {
        return handle(super.createEndOfEarlyData());
    }

    @Override
    public Finished createFinished(byte[] verify_data) {
        return handle(super.createFinished(verify_data));
    }

    @Override
    public HelloRetryRequest createHelloRetryRequest() {
        return handle(super.createHelloRetryRequest());
    }

    @Override
    public ServerHello createServerHello(ProtocolVersion version,
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

        private final HandshakeDeepFuzzer fuzzer;
        private final Class clazz;
        private HandshakeMessage message;

        MessageAction(HandshakeDeepFuzzer fuzzer, HandshakeMessage message) {
            this.fuzzer = fuzzer;
            this.message = message;
            this.clazz = message.getClass();
        }

        public MessageAction fuzz() {
            if (fuzzer.mode == Mode.fuzzing) {
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
}
