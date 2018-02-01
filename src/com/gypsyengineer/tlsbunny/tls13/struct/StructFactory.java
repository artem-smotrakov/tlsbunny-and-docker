package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.util.Collections;
import java.util.List;

public class StructFactory {
    
    public static final byte[] EMPTY_SESSION_ID = Utils.EMPTY_ARRAY;
    public static final List<Extension> NO_EXTENSIONS = Collections.EMPTY_LIST;
    
    public static StructFactory getDefault() {
        return new StructFactory();
    }

    public TLSPlaintext createTLSPlaintext(
            ContentType type, ProtocolVersion version, byte[] content) {
        
        return new TLSPlaintext(type, version, 
                new UInt16(content.length), new Bytes(content));
    }
    
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
    
    public TLSInnerPlaintext createTLSInnerPlaintext(
            ContentType type, byte[] content, byte[] zeros) {
        
        return new TLSInnerPlaintext(new Bytes(content), type, new Bytes(zeros));
    }
    
    public Handshake createHandshake(HandshakeType type, byte[] content) {
        return new Handshake(type, new UInt24(content.length), new Bytes(content));
    }
    
    public Alert createAlert(AlertLevel level, AlertDescription description) {
        return new Alert(level, description);
    }
    
    // handshake messages below
    
    public ClientHello createClientHello(
            ProtocolVersion legacy_version,
            Random random,
            byte[] legacy_session_id,
            List<CipherSuite> cipher_suites,
            List<CompressionMethod> legacy_compression_methods,
            List<Extension> extensions) {
        
        return new ClientHello(
                legacy_version, 
                random, 
                Vector.wrap(ClientHello.LEGACY_SESSION_ID_LENGTH_BYTES, legacy_session_id),
                Vector.wrap(ClientHello.CIPHER_SUITES_LENGTH_BYTES, cipher_suites), 
                Vector.wrap(ClientHello.LEGACY_COMPRESSION_METHODS_LENGTH_BYTES, legacy_compression_methods),
                Vector.wrap(ClientHello.EXTENSIONS_LENGTH_BYTES, extensions));
    }
    
    public ServerHello createServerHello() {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    public HelloRetryRequest createHelloRetryRequest() {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    public EncryptedExtensions createEncryptedExtensions(Extension... extensions) {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    public EndOfEarlyData createEndOfEarlyData() {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    public CertificateVerify createCertificateVerify(
            SignatureScheme algorithm, byte[] signature) {
        
        return new CertificateVerify(
                algorithm,
                Vector.wrap(CertificateVerify.SIGNATURE_LENGTH_BYTES, signature));
    }
    
    public CertificateRequest createCertificateRequest() {
        throw new UnsupportedOperationException("I don't know how to do it yet!");
    }
    
    public Certificate createCertificate(
            byte[] certificate_request_context,
            CertificateEntry... certificate_list) {
        
        return new Certificate(
                Vector.wrap(Certificate.CONTEXT_LENGTH_BYTES, certificate_request_context),
                Vector.wrap(Certificate.CERTIFICATE_LIST_LENGTH_BYTES, certificate_list));
    }
    
    public Finished createFinished(byte[] verify_data) {
        return new Finished(new Bytes(verify_data));
    }
}
