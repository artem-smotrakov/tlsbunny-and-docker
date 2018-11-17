package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.StructFactoryImpl;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.util.List;

public interface StructFactory {
    
    static StructFactory getDefault() {
        return new StructFactoryImpl();
    }

    byte[] empty_session_id = Utils.empty_array;

    CompressionMethod createCompressionMethod(int code);
    CipherSuite createCipherSuite(int first, int second);
    HkdfLabel createHkdfLabel(int length, byte[] label, byte[] hashValue);
    UncompressedPointRepresentation createUncompressedPointRepresentation(byte[] X,
                                                                          byte[] Y);
    HandshakeType createHandshakeType(int code);
    ProtocolVersion createProtocolVersion(int minor, int major);
    ExtensionType createExtensionType(int code);
    ContentType createContentType(int code);
    SignatureScheme createSignatureScheme(int code);
    NamedGroup.FFDHE createFFDHENamedGroup(int code);
    NamedGroup.Secp createSecpNamedGroup(int code, String curve);
    NamedGroup.X createXNamedGroup(int code);
    KeyShareEntry createKeyShareEntry(NamedGroup group, byte[] bytes);
    
    TLSInnerPlaintext createTLSInnerPlaintext(ContentType type,
                                              byte[] content,
                                              byte[] zeros);
    TLSPlaintext createTLSPlaintext(ContentType type,
                                    ProtocolVersion version,
                                    byte[] content);
    TLSPlaintext[] createTLSPlaintexts(ContentType type,
                                       ProtocolVersion version,
                                       byte[] content);

    AlertLevel createAlertLevel(int code);
    AlertDescription createAlertDescription(int code);
    Alert createAlert(AlertLevel level, AlertDescription description);

    ChangeCipherSpec createChangeCipherSpec(int value);
    
    // handshake messages
    Handshake createHandshake(HandshakeType type, byte[] content);

    Certificate createCertificate(Vector<Byte> certificate_request_context,
                                  Vector<CertificateEntry> certificate_list);

    default Certificate createCertificate(byte[] certificate_request_context,
                                  CertificateEntry... certificate_list) {

        return createCertificate(
                Vector.wrap(Certificate.CONTEXT_LENGTH_BYTES, certificate_request_context),
                Vector.wrap(Certificate.CERTIFICATE_LIST_LENGTH_BYTES, certificate_list));
    }

    CertificateRequest createCertificateRequest(byte[] certificate_request_context,
                                                Vector<Extension> extensions);

    default CertificateRequest createCertificateRequest(byte[] certificate_request_context,
                                                List<Extension> extensions) {

        return createCertificateRequest(
                certificate_request_context,
                Vector.wrap(
                        CertificateRequest.EXTENSIONS_LENGTH_BYTES,
                        extensions));
    }

    CertificateVerify createCertificateVerify(SignatureScheme algorithm,
                                              byte[] signature);

    ClientHello createClientHello(ProtocolVersion legacy_version,
                                  Random random,
                                  byte[] legacy_session_id,
                                  List<CipherSuite> cipher_suites,
                                  List<CompressionMethod> legacy_compression_methods,
                                  List<Extension> extensions);

    EncryptedExtensions createEncryptedExtensions(Vector<Extension> extensions);
    EndOfEarlyData createEndOfEarlyData();
    Finished createFinished(byte[] verify_data);
    HelloRetryRequest createHelloRetryRequest();

    default EncryptedExtensions createEncryptedExtensions(Extension... extensions) {
        return createEncryptedExtensions(
                Vector.wrap(EncryptedExtensions.LENGTH_BYTES, extensions));
    }

    default ServerHello createServerHello(ProtocolVersion version,
                                  Random random,
                                  byte[] legacy_session_id_echo,
                                  CipherSuite cipher_suite,
                                  CompressionMethod legacy_compression_method,
                                  List<Extension> extensions) {

        return createServerHello(
                version,
                random,
                Vector.wrap(
                        ServerHello.LEGACY_SESSION_ID_ECHO_LENGTH_BYTES,
                        legacy_session_id_echo),
                cipher_suite,
                legacy_compression_method,
                Vector.wrap(
                        ServerHello.EXTENSIONS_LENGTH_BYTES,
                        extensions));
    }

    ServerHello createServerHello(ProtocolVersion version,
                                  Random random,
                                  Vector<Byte> legacy_session_id_echo,
                                  CipherSuite cipher_suite,
                                  CompressionMethod legacy_compression_method,
                                  Vector<Extension> extensions);
    
    // create extensions
    KeyShare.ClientHello createKeyShareForClientHello(KeyShareEntry... entries);
    KeyShare.ServerHello createKeyShareForServerHello(KeyShareEntry entry);
    SupportedVersions.ClientHello createSupportedVersionForClientHello(ProtocolVersion version);
    SupportedVersions.ServerHello createSupportedVersionForServerHello(ProtocolVersion version);
    SignatureSchemeList createSignatureSchemeList(SignatureScheme scheme);
    NamedGroupList createNamedGroupList(NamedGroup... groups);
    CertificateEntry.X509 createX509CertificateEntry(byte[] bytes);
    Extension createExtension(ExtensionType type, byte[] bytes);
    Cookie createCookie(Vector<Byte> cookie);
    Cookie createCookie(byte[] cookie);
    MaxFragmentLength createMaxFragmentLength(int code);
    CertificateStatusType createCertificateStatusType(int code);
    OCSPStatusRequest createOCSPStatusRequest(Vector<ResponderID> responder_id_list,
                                              Vector<Byte> extensions);
    CertificateStatusRequest createCertificateStatusRequest(CertificateStatusType status_type,
                                                            OCSPStatusRequest request);
    
    StructParser parser();
}
