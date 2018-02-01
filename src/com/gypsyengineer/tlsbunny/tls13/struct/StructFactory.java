/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateVerifyImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CompressionMethodImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateEntryImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.AlertDescriptionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ContentTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CipherSuiteImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateRequestImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.AlertImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ClientHelloImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.AlertLevelImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.EncryptedExtensionsImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.FinishedImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.EndOfEarlyDataImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ProtocolVersionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HelloRetryRequestImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ServerHelloImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.TLSInnerPlaintextImpl;
import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.TLSPlaintextImpl;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author artem
 */
public interface StructFactory {

    byte[] EMPTY_SESSION_ID = Utils.EMPTY_ARRAY;
    List<ExtensionImpl> NO_EXTENSIONS = Collections.EMPTY_LIST;

    AlertImpl createAlert(AlertLevelImpl level, AlertDescriptionImpl description);

    CertificateImpl createCertificate(byte[] certificate_request_context, CertificateEntryImpl... certificate_list);

    CertificateRequestImpl createCertificateRequest();

    CertificateVerifyImpl createCertificateVerify(SignatureSchemeImpl algorithm, byte[] signature);

    // handshake messages below
    ClientHelloImpl createClientHello(ProtocolVersionImpl legacy_version, Random random, byte[] legacy_session_id, List<CipherSuiteImpl> cipher_suites, List<CompressionMethodImpl> legacy_compression_methods, List<ExtensionImpl> extensions);

    EncryptedExtensionsImpl createEncryptedExtensions(ExtensionImpl... extensions);

    EndOfEarlyDataImpl createEndOfEarlyData();

    FinishedImpl createFinished(byte[] verify_data);

    HandshakeImpl createHandshake(HandshakeTypeImpl type, byte[] content);

    HelloRetryRequestImpl createHelloRetryRequest();

    ServerHelloImpl createServerHello();

    TLSInnerPlaintextImpl createTLSInnerPlaintext(ContentTypeImpl type, byte[] content, byte[] zeros);

    TLSPlaintextImpl createTLSPlaintext(ContentTypeImpl type, ProtocolVersionImpl version, byte[] content);

    TLSPlaintextImpl[] createTLSPlaintexts(ContentTypeImpl type, ProtocolVersionImpl version, byte[] content);
    
    SupportedVersions.ClientHello createSupportedVersionForClientHello(ProtocolVersion version);
}
