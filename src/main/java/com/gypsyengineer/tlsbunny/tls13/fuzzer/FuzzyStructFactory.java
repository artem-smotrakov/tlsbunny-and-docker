package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.List;

public abstract class FuzzyStructFactory<T> implements StructFactory, Fuzzer<T> {

    public static final String DEFAULT_START_TEST = "0";
    public static final String STATE_DELIMITER = ":";

    Target target;
    Mode mode;
    Output output;
    final StructFactory factory;
    Fuzzer<T> fuzzer;

    public FuzzyStructFactory(StructFactory factory, Output output) {
        this.factory = factory;
        this.output = output;
    }

    public FuzzyStructFactory target(Target target) {
        this.target = target;
        return this;
    }

    public FuzzyStructFactory target(String target) {
        return target(Target.valueOf(target));
    }

    public FuzzyStructFactory mode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public FuzzyStructFactory mode(String mode) {
        return mode(Mode.valueOf(mode));
    }

    abstract void initFuzzer(String state);

    // implement methods from Fuzzer

    @Override
    public void setOutput(Output output) {
        this.output = output;
    }

    @Override
    public Output getOutput() {
        return output;
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
            throw new IllegalArgumentException(
                    "what the hell? state should not be null!");
        }

        state = state.toLowerCase().trim();
        if (state.isEmpty()) {
            throw new IllegalArgumentException(
                    "what the hell? state should not be empty!");
        }

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
                throw new IllegalArgumentException(
                        String.format("what the hell? invalid state: %s", state));
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

    // override methods from StructFactory

    @Override
    public CompressionMethod createCompressionMethod(int code) {
        return factory.createCompressionMethod(code);
    }

    @Override
    public CipherSuite createCipherSuite(int first, int second) {
        return factory.createCipherSuite(first, second);
    }

    @Override
    public HkdfLabel createHkdfLabel(int length, byte[] label, byte[] hashValue) {
        return factory.createHkdfLabel(length, label, hashValue);
    }

    @Override
    public UncompressedPointRepresentation createUncompressedPointRepresentation(
            byte[] X, byte[] Y) {

        return factory.createUncompressedPointRepresentation(X, Y);
    }

    @Override
    public HandshakeType createHandshakeType(int code) {
        return factory.createHandshakeType(code);
    }

    @Override
    public ProtocolVersion createProtocolVersion(int minor, int major) {
        return factory.createProtocolVersion(minor, major);
    }

    @Override
    public ExtensionType createExtensionType(int code) {
        return factory.createExtensionType(code);
    }

    @Override
    public ContentType createContentType(int code) {
        return factory.createContentType(code);
    }

    @Override
    public SignatureScheme createSignatureScheme(int code) {
        return factory.createSignatureScheme(code);
    }

    @Override
    public NamedGroup.FFDHE createFFDHENamedGroup(int code) {
        return factory.createFFDHENamedGroup(code);
    }

    @Override
    public NamedGroup.Secp createSecpNamedGroup(int code, String curve) {
        return factory.createSecpNamedGroup(code, curve);
    }

    @Override
    public NamedGroup.X createXNamedGroup(int code) {
        return factory.createXNamedGroup(code);
    }

    @Override
    public KeyShareEntry createKeyShareEntry(NamedGroup group, byte[] bytes) {
        return factory.createKeyShareEntry(group, bytes);
    }

    @Override
    public TLSInnerPlaintext createTLSInnerPlaintext(
            ContentType type, byte[] content, byte[] zeros) {

        return factory.createTLSInnerPlaintext(type, content, zeros);
    }

    @Override
    public TLSPlaintext createTLSPlaintext(
            ContentType type, ProtocolVersion version, byte[] content) {

        return factory.createTLSPlaintext(type, version, content);
    }

    @Override
    public TLSPlaintext[] createTLSPlaintexts(
            ContentType type, ProtocolVersion version, byte[] content) {

        return factory.createTLSPlaintexts(type, version, content);
    }

    @Override
    public AlertLevel createAlertLevel(int code) {
        return factory.createAlertLevel(code);
    }

    @Override
    public AlertDescription createAlertDescription(int code) {
        return factory.createAlertDescription(code);
    }

    @Override
    public Alert createAlert(AlertLevel level, AlertDescription description) {
        return factory.createAlert(level, description);
    }

    @Override
    public ChangeCipherSpec createChangeCipherSpec(int value) {
        return factory.createChangeCipherSpec(value);
    }

    @Override
    public Handshake createHandshake(HandshakeType type, byte[] content) {
        return factory.createHandshake(type, content);
    }

    @Override
    public Certificate createCertificate(
            byte[] certificate_request_context, CertificateEntry... certificate_list) {

        return factory.createCertificate(certificate_request_context, certificate_list);
    }

    @Override
    public CertificateRequest createCertificateRequest() {
        return factory.createCertificateRequest();
    }

    @Override
    public CertificateVerify createCertificateVerify(
            SignatureScheme algorithm, byte[] signature) {

        return factory.createCertificateVerify(algorithm, signature);
    }

    @Override
    public ClientHello createClientHello(ProtocolVersion legacy_version,
                                         Random random,
                                         byte[] legacy_session_id,
                                         List<CipherSuite> cipher_suites,
                                         List<CompressionMethod> legacy_compression_methods,
                                         List<Extension> extensions) {

        return factory.createClientHello(legacy_version, random, legacy_session_id,
                cipher_suites, legacy_compression_methods, extensions);
    }

    @Override
    public ClientHello createClientHello(ProtocolVersion legacy_version,
                                         Random random,
                                         Vector<Byte> legacy_session_id,
                                         Vector<CipherSuite> cipher_suites,
                                         Vector<CompressionMethod> legacy_compression_methods,
                                         Vector<Extension> extensions) {

        return factory.createClientHello(legacy_version, random, legacy_session_id,
                cipher_suites, legacy_compression_methods, extensions);
    }

    @Override
    public EncryptedExtensions createEncryptedExtensions(Extension... extensions) {
        return factory.createEncryptedExtensions(extensions);
    }

    @Override
    public EndOfEarlyData createEndOfEarlyData() {
        return factory.createEndOfEarlyData();
    }

    @Override
    public Finished createFinished(byte[] verify_data) {
        return factory.createFinished(verify_data);
    }

    @Override
    public HelloRetryRequest createHelloRetryRequest() {
        return factory.createHelloRetryRequest();
    }

    @Override
    public ServerHello createServerHello(ProtocolVersion version,
                                         Random random,
                                         byte[] legacy_session_id_echo,
                                         CipherSuite cipher_suite,
                                         CompressionMethod legacy_compression_method,
                                         List<Extension> extensions) {

        return factory.createServerHello(version, random, legacy_session_id_echo,
                cipher_suite, legacy_compression_method, extensions);
    }

    @Override
    public KeyShare.ClientHello createKeyShareForClientHello(KeyShareEntry... entries) {
        return factory.createKeyShareForClientHello(entries);
    }

    @Override
    public SupportedVersions.ClientHello createSupportedVersionForClientHello(
            ProtocolVersion version) {

        return factory.createSupportedVersionForClientHello(version);
    }

    @Override
    public SignatureSchemeList createSignatureSchemeList(SignatureScheme scheme) {
        return factory.createSignatureSchemeList(scheme);
    }

    @Override
    public NamedGroupList createNamedGroupList(NamedGroup... groups) {
        return factory.createNamedGroupList(groups);
    }

    @Override
    public CertificateEntry.X509 createX509CertificateEntry(byte[] bytes) {
        return factory.createX509CertificateEntry(bytes);
    }

    @Override
    public Extension createExtension(ExtensionType type, byte[] bytes) {
        return factory.createExtension(type, bytes);
    }

    @Override
    public StructParser parser() {
        return factory.parser();
    }


}
