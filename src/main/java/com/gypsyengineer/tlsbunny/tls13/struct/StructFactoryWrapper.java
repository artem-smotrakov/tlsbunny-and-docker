package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Random;
import java.util.List;

public class StructFactoryWrapper implements StructFactory {

    public final StructFactory factory;

    public StructFactoryWrapper(StructFactory factory) {
        this.factory = factory;
    }
    
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
    public ProtocolVersion createProtocolVestion(int minor, int major) {
        return factory.createProtocolVestion(minor, major);
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
            Random random, byte[] legacy_session_id, List<CipherSuite> cipher_suites,
            List<CompressionMethod> legacy_compression_methods, List<Extension> extensions) {

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
    public ServerHello createServerHello(
            ProtocolVersion version, Random random, byte[] legacy_session_id_echo,
            CipherSuite cipher_suite, CompressionMethod legacy_compression_method,
            List<Extension> extensions) {

        return factory.createServerHello(version, random, legacy_session_id_echo,
                cipher_suite, legacy_compression_method, extensions);
    }

    @Override
    public KeyShare.ClientHello createKeyShareForClientHello(KeyShareEntry... entries) {
        return factory.createKeyShareForClientHello(entries);
    }

    @Override
    public SupportedVersions.ClientHello createSupportedVersionForClientHello(ProtocolVersion version) {
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
