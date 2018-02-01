package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.*; // TODO: replace * with acutal types 
import com.gypsyengineer.tlsbunny.tls13.struct.AlertDescription;
import com.gypsyengineer.tlsbunny.tls13.struct.AlertLevel;
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
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

// TODO: make all implementations immutable
public class StructFactoryImpl implements StructFactory {
    
    public static StructFactory getDefault() {
        return new StructFactoryImpl();
    }

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
    
    public Extension wrap(SupportedVersions supportedVersions) throws IOException {
        return wrap(ExtensionTypeImpl.supported_versions, supportedVersions.encoding());
    }
    
    public Extension wrap(SignatureSchemeList signatureSchemeList) throws IOException {
        return wrap(ExtensionTypeImpl.signature_algorithms, signatureSchemeList.encoding());
    }
    
    public Extension wrap(NamedGroupList namedGroupList) throws IOException {
        return wrap(ExtensionTypeImpl.supported_groups, namedGroupList.encoding());
    }
    
    public Extension wrap(KeyShare keyShare) throws IOException {
        return wrap(ExtensionTypeImpl.key_share, keyShare.encoding());
    }
    
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
        return KeyShareImpl.ClientHelloImpl.create(entry);
    }

    @Override
    public SignatureSchemeList createSignatureSchemeList(SignatureScheme scheme) {
        return SignatureSchemeListImpl.create(scheme);
    }

    @Override
    public NamedGroupList createNamedGroupList(NamedGroup group) {
        return NamedGroupListImpl.create(group);
    }

    @Override
    public ContentType parseContentType(byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProtocolVersion parseProtocolVersion(byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CipherSuite parseCipherSuite(byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Alert parseAlert(byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AlertLevel parseAlertLevel(byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AlertDescription parseAlertDescription(byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Certificate parseCertificate(byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CertificateRequest parseCertificateRequest(byte[] bytes) {
        return CertificateRequestImpl.parse(bytes);
    }

    @Override
    public CertificateVerify parseCertificateVerify(byte[] bytes) {
        return CertificateVerifyImpl.parse(bytes); // TODO
    }

    @Override
    public ClientHello parseClientHello(byte[] bytes) {
        return ClientHelloImpl.parse(bytes);
    }

    @Override
    public EncryptedExtensions parseEncryptedExtensions(byte[] bytes) {
        return EncryptedExtensionsImpl.parse(bytes);
    }

    @Override
    public EndOfEarlyData parseEndOfEarlyData(byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Finished parseFinished(byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Handshake parseHandshake(ByteBuffer buffer) {
        return HandshakeImpl.parse(buffer); // TODO
    }

    @Override
    public HelloRetryRequest parseHelloRetryRequest(byte[] bytes) {
        return HelloRetryRequestImpl.parse(bytes); // TODO
    }

    @Override
    public ServerHello parseServerHello(byte[] bytes) {
        return ServerHelloImpl.parse(bytes); // TODO
    }

    @Override
    public TLSInnerPlaintext parseTLSInnerPlaintext(byte[] bytes) {
        return TLSInnerPlaintextImpl.parse(bytes); // TODO
    }

    @Override
    public TLSPlaintext parseTLSPlaintext(ByteBuffer buffer) {
        return TLSPlaintextImpl.parse(buffer); // TODO
    }
}
