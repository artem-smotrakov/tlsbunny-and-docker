package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.Alert;
import com.gypsyengineer.tlsbunny.tls13.struct.AlertDescription;
import com.gypsyengineer.tlsbunny.tls13.struct.AlertLevel;
import com.gypsyengineer.tlsbunny.tls13.struct.Certificate;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateVerify;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.CompressionMethod;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.SupportedVersions;
import com.gypsyengineer.tlsbunny.tls13.struct.ClientHello;
import com.gypsyengineer.tlsbunny.tls13.struct.EncryptedExtensions;
import com.gypsyengineer.tlsbunny.tls13.struct.EndOfEarlyData;
import com.gypsyengineer.tlsbunny.tls13.struct.ExtensionType;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import com.gypsyengineer.tlsbunny.tls13.struct.HelloRetryRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.HkdfLabel;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShare;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroupList;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureSchemeList;
import com.gypsyengineer.tlsbunny.tls13.struct.StructParser;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.UncompressedPointRepresentation;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.util.List;

// TODO: all implementation should return immutable vectors
public class StructFactoryImpl implements StructFactory {

    @Override
    public TLSPlaintext createTLSPlaintext(
            ContentType type, ProtocolVersion version, byte[] content) {
        
        return new TLSPlaintextImpl(type, version, 
                new UInt16(content.length), new Bytes(content));
    }
    
    @Override
    public TLSPlaintext[] createTLSPlaintexts(
            ContentType type, ProtocolVersion version, byte[] content) {
        
        if (content.length <= TLSPlaintext.MAX_ALLOWED_LENGTH) {
            return new TLSPlaintext[] {
                createTLSPlaintext(type, version, content)
            };
        }

        byte[][] fragments = Utils.split(content, TLSPlaintext.MAX_ALLOWED_LENGTH);
        TLSPlaintext[] tlsPlaintexts = new TLSPlaintext[fragments.length];
        for (int i=0; i < fragments.length; i++) {
            tlsPlaintexts[i] = createTLSPlaintext(type, version, fragments[i]);
        }

        return tlsPlaintexts;
    }
    
    @Override
    public TLSInnerPlaintext createTLSInnerPlaintext(
            ContentType type, byte[] content, byte[] zeros) {
        
        return new TLSInnerPlaintextImpl(new Bytes(content), type, new Bytes(zeros));
    }
    
    @Override
    public Handshake createHandshake(HandshakeType type, byte[] content) {
        return new HandshakeImpl(type, new UInt24(content.length), new Bytes(content));
    }
    
    @Override
    public Alert createAlert(AlertLevel level, AlertDescription description) {
        return new AlertImpl(level, description);
    }
    
    // handshake messages below
    
    @Override
    public ClientHello createClientHello(
            ProtocolVersion legacy_version,
            Random random,
            byte[] legacy_session_id,
            List<CipherSuite> cipher_suites,
            List<CompressionMethod> legacy_compression_methods,
            List<Extension> extensions) {
        
        return new ClientHelloImpl(
                legacy_version, 
                random, 
                Vector.wrap(ClientHelloImpl.LEGACY_SESSION_ID_LENGTH_BYTES, legacy_session_id),
                Vector.wrap(ClientHelloImpl.CIPHER_SUITES_LENGTH_BYTES, cipher_suites), 
                Vector.wrap(ClientHelloImpl.LEGACY_COMPRESSION_METHODS_LENGTH_BYTES, legacy_compression_methods),
                Vector.wrap(ClientHelloImpl.EXTENSIONS_LENGTH_BYTES, extensions));
    }
    
    @Override
    public ServerHello createServerHello() {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    @Override
    public HelloRetryRequest createHelloRetryRequest() {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    @Override
    public EncryptedExtensions createEncryptedExtensions(Extension... extensions) {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    @Override
    public EndOfEarlyData createEndOfEarlyData() {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    @Override
    public CertificateVerify createCertificateVerify(
            SignatureScheme algorithm, byte[] signature) {
        
        return new CertificateVerifyImpl(
                algorithm,
                Vector.wrap(CertificateVerifyImpl.SIGNATURE_LENGTH_BYTES, signature));
    }
    
    @Override
    public CertificateRequest createCertificateRequest() {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    @Override
    public Certificate createCertificate(
            byte[] certificate_request_context,
            CertificateEntry... certificate_list) {
        
        return new CertificateImpl(
                Vector.wrap(CertificateImpl.CONTEXT_LENGTH_BYTES, certificate_request_context),
                Vector.wrap(CertificateImpl.CERTIFICATE_LIST_LENGTH_BYTES, certificate_list));
    }
    
    @Override
    public Finished createFinished(byte[] verify_data) {
        return new FinishedImpl(new Bytes(verify_data));
    }

    @Override
    public SupportedVersions.ClientHello createSupportedVersionForClientHello(ProtocolVersion version) {
        return new SupportedVersionsImpl.ClientHelloImpl(
                Vector.wrap(SupportedVersions.ClientHello.VERSIONS_LENGTH_BYTES, version));
    }
    
    @Override
    public Extension wrap(SupportedVersions supportedVersions) throws IOException {
        return wrap(ExtensionType.supported_versions, supportedVersions.encoding());
    }
    
    @Override
    public Extension wrap(SignatureSchemeList signatureSchemeList) throws IOException {
        return wrap(ExtensionType.signature_algorithms, signatureSchemeList.encoding());
    }
    
    @Override
    public Extension wrap(NamedGroupList namedGroupList) throws IOException {
        return wrap(ExtensionType.supported_groups, namedGroupList.encoding());
    }
    
    @Override
    public Extension wrap(KeyShare keyShare) throws IOException {
        return wrap(ExtensionType.key_share, keyShare.encoding());
    }
    
    @Override
    public Extension wrap(ExtensionType type, byte[] bytes) {
        return new ExtensionImpl(
                type, 
                Vector.wrap(Extension.EXTENSION_DATA_LENGTH_BYTES, bytes));
    }

    @Override
    public CompressionMethod createCompressionMethod(int code) {
        return new CompressionMethodImpl(code);
    }

    @Override
    public KeyShare.ClientHello createKeyShareForClientHello(KeyShareEntry entry) {
        return new KeyShareImpl.ClientHelloImpl(
                    Vector.wrap(
                            KeyShare.ClientHello.LENGTH_BYTES, 
                            entry));
    }

    @Override
    public SignatureSchemeList createSignatureSchemeList(SignatureScheme scheme) {
        return new SignatureSchemeListImpl(
                Vector.wrap(SignatureSchemeList.LENGTH_BYTES, scheme));
    }

    @Override
    public NamedGroupList createNamedGroupList(NamedGroup group) {
        return new NamedGroupListImpl(
                Vector.wrap(NamedGroupList.LENGTH_BYTES, group));
    }

    @Override
    public AlertLevel createAlertLevel(int code) {
        return new AlertLevelImpl(code);
    }

    @Override
    public AlertDescription createAlertDescription(int code) {
        return new AlertDescriptionImpl(code);
    }

    @Override
    public HkdfLabel createHkdfLabel(int length, byte[] label, byte[] hashValue) {
        return new HkdfLabelImpl(
                new UInt16(length),
                Vector.wrap(HkdfLabel.LABEL_LENGTH_BYTES, label),
                Vector.wrap(HkdfLabel.HASH_VALUE_LENGTH_BYTES, hashValue));
    }

    @Override
    public CipherSuite createCipherSuite(int first, int second) {
        return new CipherSuiteImpl(first, second);
    }

    @Override
    public UncompressedPointRepresentation createUncompressedPointRepresentation(
            byte[] X, byte[] Y) {
        
        return new UncompressedPointRepresentationImpl(X, Y);
    }

    @Override
    public HandshakeType createHandshakeType(int code) {
        return new HandshakeTypeImpl(code);
    }

    @Override
    public ProtocolVersion createProtocolVestion(int minor, int major) {
        return new ProtocolVersionImpl(minor, major);
    }

    @Override
    public ExtensionType createExtensionType(int code) {
        return new ExtensionTypeImpl(code);
    }

    @Override
    public ContentType createContentType(int code) {
        return new ContentTypeImpl(code);
    }

    @Override
    public SignatureScheme createSignatureScheme(int code) {
        return new SignatureSchemeImpl(code);
    }

    @Override
    public NamedGroup.FFDHE createFFDHENamedGroup(int code) {
        return new NamedGroupImpl.FFDHEImpl(code);
    }

    @Override
    public NamedGroup.Secp createSecpNamedGroup(int code, String curve) {
        return new NamedGroupImpl.SecpImpl(code, curve);
    }

    @Override
    public NamedGroup.X createXNamedGroup(int code) {
        return new NamedGroupImpl.XImpl(code);
    }

    @Override
    public KeyShareEntry createKeyShareEntry(NamedGroup group, byte[] bytes) {
        return new KeyShareEntryImpl(
                group, 
                Vector.wrap(KeyShareEntry.KEY_EXCHANGE_LENGTH_BYTES, bytes));
    }
    
    @Override
    public CertificateEntry.X509 createX509CertificateEntry(byte[] bytes) {
        return new CertificateEntryImpl.X509Impl(
                    Vector.wrap(CertificateEntry.X509.LENGTH_BYTES, bytes), 
                    Vector.wrap(CertificateEntry.X509.EXTENSIONS_LENGTH_BYTES));
    }

    @Override
    public StructParser parser() {
        return new StructParserImpl();
    }
    
}
