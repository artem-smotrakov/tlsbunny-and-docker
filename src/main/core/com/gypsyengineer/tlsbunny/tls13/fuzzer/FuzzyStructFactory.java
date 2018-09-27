package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.fuzzer.Fuzzer;
import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.List;

public abstract class FuzzyStructFactory<T> implements StructFactory, Fuzzer<T> {

    protected final StructFactory factory;

    protected Target target;
    protected Output output;
    protected Fuzzer<T> fuzzer;

    public FuzzyStructFactory(StructFactory factory, Output output) {
        this.factory = factory;
        this.output = output;
    }

    synchronized public FuzzyStructFactory target(Target target) {
        this.target = target;
        return this;
    }

    synchronized public FuzzyStructFactory target(String target) {
        return target(Target.valueOf(target));
    }

    synchronized public Target target() {
        return target;
    }

    synchronized public FuzzyStructFactory<T> fuzzer(Fuzzer<T> fuzzer) {
        this.fuzzer = fuzzer;
        return this;
    }

    // implement methods from Fuzzer

    @Override
    synchronized public FuzzyStructFactory set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    synchronized public Output output() {
        return output;
    }

    @Override
    synchronized public long currentTest() {
        return fuzzer.currentTest();
    }

    @Override
    synchronized public void currentTest(long test) {
        fuzzer.currentTest(test);
    }

    @Override
    synchronized public boolean canFuzz() {
        return fuzzer.canFuzz();
    }

    @Override
    synchronized public void moveOn() {
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
    public Cookie createCookie(Vector<Byte> cookie) {
        return factory.createCookie(cookie);
    }

    @Override
    public Cookie createCookie(byte[] cookie) {
        return factory.createCookie(cookie);
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
    public CertificateRequest createCertificateRequest(byte[] certificate_request_context,
                                                Vector<Extension> extensions) {

        return factory.createCertificateRequest(certificate_request_context, extensions);
    }

    @Override
    public CertificateRequest createCertificateRequest(byte[] certificate_request_context,
                                                List<Extension> extensions) {

        return factory.createCertificateRequest(certificate_request_context, extensions);
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
    public ServerHello createServerHello(ProtocolVersion version,
                                         Random random,
                                         Vector<Byte> legacy_session_id_echo,
                                         CipherSuite cipher_suite,
                                         CompressionMethod legacy_compression_method,
                                         Vector<Extension> extensions) {

        return factory.createServerHello(version, random, legacy_session_id_echo,
                cipher_suite, legacy_compression_method, extensions);
    }

    @Override
    public KeyShare.ClientHello createKeyShareForClientHello(KeyShareEntry... entries) {
        return factory.createKeyShareForClientHello(entries);
    }

    @Override
    public KeyShare.ServerHello createKeyShareForServerHello(KeyShareEntry entry) {
        return factory.createKeyShareForServerHello(entry);
    }

    @Override
    public SupportedVersions.ClientHello createSupportedVersionForClientHello(
            ProtocolVersion version) {

        return factory.createSupportedVersionForClientHello(version);
    }

    @Override
    public SupportedVersions.ServerHello createSupportedVersionForServerHello(
            ProtocolVersion version) {

        return factory.createSupportedVersionForServerHello(version);
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
    public MaxFragmentLength createMaxFragmentLength(int code) {
        return factory.createMaxFragmentLength(code);
    }

    @Override
    public CertificateStatusType createCertificateStatusType(int code) {
        return factory.createCertificateStatusType(code);
    }

    @Override
    public OCSPStatusRequest createOCSPStatusRequest(Vector<ResponderID> responder_id_list,
                                                     Vector<Byte> extensions) {
        return factory.createOCSPStatusRequest(responder_id_list, extensions);
    }

    @Override
    public CertificateStatusRequest createCertificateStatusRequest(CertificateStatusType status_type,
                                                                   OCSPStatusRequest request) {
        return factory.createCertificateStatusRequest(status_type, request);
    }

    @Override
    public StructParser parser() {
        return factory.parser();
    }


}
