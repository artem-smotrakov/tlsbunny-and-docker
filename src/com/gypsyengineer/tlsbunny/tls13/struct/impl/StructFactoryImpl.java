package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.SupportedVersions;
import static com.gypsyengineer.tlsbunny.tls13.struct.SupportedVersions.ClientHello.VERSIONS_LENGTH_BYTES;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.util.Collections;
import java.util.List;

// TODO: make all implementations immutable
public class StructFactoryImpl implements StructFactory {
    
    
    public static StructFactoryImpl getDefault() {
        return new StructFactoryImpl();
    }

    @Override
    public TLSPlaintextImpl createTLSPlaintext(
            ContentTypeImpl type, ProtocolVersionImpl version, byte[] content) {
        
        return new TLSPlaintextImpl(type, version, 
                new UInt16(content.length), new Bytes(content));
    }
    
    @Override
    public TLSPlaintextImpl[] createTLSPlaintexts(
            ContentTypeImpl type, ProtocolVersionImpl version, byte[] content) {
        
        if (content.length <= TLSPlaintext.MAX_ALLOWED_LENGTH) {
            return new TLSPlaintextImpl[] {
                createTLSPlaintext(type, version, content)
            };
        }

        byte[][] fragments = Utils.split(content, TLSPlaintext.MAX_ALLOWED_LENGTH);
        TLSPlaintextImpl[] tlsPlaintexts = new TLSPlaintextImpl[fragments.length];
        for (int i=0; i < fragments.length; i++) {
            tlsPlaintexts[i] = createTLSPlaintext(type, version, fragments[i]);
        }

        return tlsPlaintexts;
    }
    
    @Override
    public TLSInnerPlaintextImpl createTLSInnerPlaintext(
            ContentTypeImpl type, byte[] content, byte[] zeros) {
        
        return new TLSInnerPlaintextImpl(new Bytes(content), type, new Bytes(zeros));
    }
    
    @Override
    public HandshakeImpl createHandshake(HandshakeTypeImpl type, byte[] content) {
        return new HandshakeImpl(type, new UInt24(content.length), new Bytes(content));
    }
    
    @Override
    public AlertImpl createAlert(AlertLevelImpl level, AlertDescriptionImpl description) {
        return new AlertImpl(level, description);
    }
    
    // handshake messages below
    
    @Override
    public ClientHelloImpl createClientHello(
            ProtocolVersionImpl legacy_version,
            Random random,
            byte[] legacy_session_id,
            List<CipherSuiteImpl> cipher_suites,
            List<CompressionMethodImpl> legacy_compression_methods,
            List<ExtensionImpl> extensions) {
        
        return new ClientHelloImpl(
                legacy_version, 
                random, 
                Vector.wrap(ClientHelloImpl.LEGACY_SESSION_ID_LENGTH_BYTES, legacy_session_id),
                Vector.wrap(ClientHelloImpl.CIPHER_SUITES_LENGTH_BYTES, cipher_suites), 
                Vector.wrap(ClientHelloImpl.LEGACY_COMPRESSION_METHODS_LENGTH_BYTES, legacy_compression_methods),
                Vector.wrap(ClientHelloImpl.EXTENSIONS_LENGTH_BYTES, extensions));
    }
    
    @Override
    public ServerHelloImpl createServerHello() {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    @Override
    public HelloRetryRequestImpl createHelloRetryRequest() {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    @Override
    public EncryptedExtensionsImpl createEncryptedExtensions(ExtensionImpl... extensions) {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    @Override
    public EndOfEarlyDataImpl createEndOfEarlyData() {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    @Override
    public CertificateVerifyImpl createCertificateVerify(
            SignatureSchemeImpl algorithm, byte[] signature) {
        
        return new CertificateVerifyImpl(
                algorithm,
                Vector.wrap(CertificateVerifyImpl.SIGNATURE_LENGTH_BYTES, signature));
    }
    
    @Override
    public CertificateRequestImpl createCertificateRequest() {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    @Override
    public CertificateImpl createCertificate(
            byte[] certificate_request_context,
            CertificateEntryImpl... certificate_list) {
        
        return new CertificateImpl(
                Vector.wrap(CertificateImpl.CONTEXT_LENGTH_BYTES, certificate_request_context),
                Vector.wrap(CertificateImpl.CERTIFICATE_LIST_LENGTH_BYTES, certificate_list));
    }
    
    @Override
    public FinishedImpl createFinished(byte[] verify_data) {
        return new FinishedImpl(new Bytes(verify_data));
    }

    @Override
    public SupportedVersions.ClientHello createSupportedVersionForClientHello(ProtocolVersion version) {
        return new SupportedVersionsImpl.ClientHelloImpl(
                Vector.wrap(VERSIONS_LENGTH_BYTES, version));
    }
}
