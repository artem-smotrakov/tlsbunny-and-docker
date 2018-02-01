package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public interface StructFactory {

    byte[] EMPTY_SESSION_ID = Utils.EMPTY_ARRAY;
    List<Extension> NO_EXTENSIONS = Collections.EMPTY_LIST;

    CompressionMethod createCompressionMethod(int code);
    
    Alert createAlert(AlertLevel level, AlertDescription description);
    Certificate createCertificate(byte[] certificate_request_context, CertificateEntry... certificate_list);
    CertificateRequest createCertificateRequest();
    CertificateVerify createCertificateVerify(SignatureScheme algorithm, byte[] signature);
    ClientHello createClientHello(ProtocolVersion legacy_version, Random random, byte[] legacy_session_id, List<CipherSuite> cipher_suites, List<CompressionMethod> legacy_compression_methods, List<Extension> extensions);
    EncryptedExtensions createEncryptedExtensions(Extension... extensions);
    EndOfEarlyData createEndOfEarlyData();
    Finished createFinished(byte[] verify_data);
    Handshake createHandshake(HandshakeType type, byte[] content);
    HelloRetryRequest createHelloRetryRequest();
    ServerHello createServerHello();
    TLSInnerPlaintext createTLSInnerPlaintext(ContentType type, byte[] content, byte[] zeros);
    TLSPlaintext createTLSPlaintext(ContentType type, ProtocolVersion version, byte[] content);
    TLSPlaintext[] createTLSPlaintexts(ContentType type, ProtocolVersion version, byte[] content);

    // create extensions
    KeyShare.ClientHello createKeyShareForClientHello(KeyShareEntry entry);
    SupportedVersions.ClientHello createSupportedVersionForClientHello(ProtocolVersion version);
    SignatureSchemeList createSignatureSchemeList(SignatureScheme scheme);
    NamedGroupList createNamedGroupList(NamedGroup group);
            
    ContentType parseContentType(byte[] bytes);
    ProtocolVersion parseProtocolVersion(byte[] bytes);
    CipherSuite parseCipherSuite(byte[] bytes);
    Alert parseAlert(byte[] bytes);
    AlertLevel parseAlertLevel(byte[] bytes);
    AlertDescription parseAlertDescription(byte[] bytes);
    Certificate parseCertificate(byte[] bytes);
    CertificateRequest parseCertificateRequest(byte[] bytes);
    CertificateVerify parseCertificateVerify(byte[] bytes);
    ClientHello parseClientHello(byte[] bytes);
    EncryptedExtensions parseEncryptedExtensions(byte[] bytes);
    EndOfEarlyData parseEndOfEarlyData(byte[] bytes);
    Finished parseFinished(byte[] bytes);
    
    default Handshake parseHandshake(byte[] bytes) {
        return parseHandshake(ByteBuffer.wrap(bytes));
    }
    Handshake parseHandshake(ByteBuffer buffer);
    
    HelloRetryRequest parseHelloRetryRequest(byte[] bytes);
    ServerHello parseServerHello(byte[] bytes);
    TLSInnerPlaintext parseTLSInnerPlaintext(byte[] bytes);
    
    default TLSPlaintext parseTLSPlaintext(byte[] bytes) {
        return parseTLSPlaintext(ByteBuffer.wrap(bytes));
    }
    TLSPlaintext parseTLSPlaintext(ByteBuffer buffer);
    
    public Extension wrap(SupportedVersions supportedVersions) throws IOException;
    public Extension wrap(SignatureSchemeList signatureSchemeList) throws IOException;
    public Extension wrap(NamedGroupList namedGroupList) throws IOException;
    public Extension wrap(KeyShare keyShare) throws IOException;
    public Extension wrap(ExtensionType type, byte[] bytes);
}
