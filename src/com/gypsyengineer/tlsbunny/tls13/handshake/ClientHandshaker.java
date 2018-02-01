package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.AlertImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateRequestImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateVerifyImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CipherSuiteImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ClientHelloImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.KeyShareImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CompressionMethodImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ContentTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.EncryptedExtensionsImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.FinishedImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HelloRetryRequestImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupListImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ServerHelloImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeListImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SupportedVersionsImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.TLSInnerPlaintextImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.TLSPlaintextImpl;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.crypto.TranscriptHash;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateEntryImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ProtocolVersionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.StructFactoryImpl;
import static com.gypsyengineer.tlsbunny.tls13.struct.impl.TLSInnerPlaintextImpl.NO_PADDING;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.CertificateHolder;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import static com.gypsyengineer.tlsbunny.utils.Utils.concatenate;
import java.io.IOException;
import java.util.List;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeMessage;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;

public class ClientHandshaker extends AbstractHandshaker {

    private final CertificateHolder clientCertificate;
    private Vector<Byte> certificate_request_context;

    ClientHandshaker(StructFactoryImpl factory, SignatureSchemeImpl scheme, NamedGroupImpl group,
            Negotiator negotiator, CipherSuiteImpl ciphersuite, HKDF hkdf,
            CertificateHolder clientCertificate) {

        super(factory, scheme, group, negotiator, ciphersuite, hkdf);
        this.clientCertificate = clientCertificate;
    }

    @Override
    public void reset() {
        super.reset();
        certificate_request_context = null;
    }

    @Override
    public ClientHelloImpl createClientHello() throws Exception {
        ClientHelloImpl hello = factory.createClientHello(ProtocolVersionImpl.TLSv12, 
                createRandom(), 
                StructFactoryImpl.EMPTY_SESSION_ID, 
                List.of(CipherSuiteImpl.TLS_AES_128_GCM_SHA256), 
                List.of(CompressionMethodImpl.createNull()), 
                StructFactoryImpl.NO_EXTENSIONS);
        
        hello.addExtension(ExtensionImpl.wrap(
                factory.createSupportedVersionForClientHello(ProtocolVersion.TLSv13)));
        hello.addExtension(ExtensionImpl.wrap(SignatureSchemeListImpl.create(scheme)));
        hello.addExtension(ExtensionImpl.wrap(NamedGroupListImpl.create(group)));
        hello.addExtension(ExtensionImpl.wrap(KeyShareImpl.ClientHelloImpl.create(negotiator.createKeyShareEntry())));

        if (!context.hasFirstClientHello()) {
            context.setFirstClientHello(hello);
        } else if (!context.hasSecondClientHello()) {
            context.setSecondClientHello(hello); 
        } else {
            throw new RuntimeException();
        }

        return hello;
    }

    @Override
    public CertificateImpl createCertificate() throws IOException {
        context.setClientCertificate(factory.createCertificate(certificate_request_context.bytes(), 
                    CertificateEntryImpl.X509.wrap(clientCertificate.getCertData())));

        return context.getClientCertificate();
    }

    @Override
    public CertificateVerifyImpl createCertificateVerify() throws Exception {
        byte[] content = Utils.concatenate(
                CERTIFICATE_VERIFY_PREFIX,
                CERTIFICATE_VERIFY_CONTEXT_STRING,
                new byte[] { 0 },
                TranscriptHash.compute(ciphersuite.hash(), allMessages()));

        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(
                KeyFactory.getInstance("EC").generatePrivate(
                        new PKCS8EncodedKeySpec(clientCertificate.getKeyData())));
        signature.update(content);

        context.setClientCertificateVerify(factory.createCertificateVerify(SignatureSchemeImpl.ecdsa_secp256r1_sha256,
                    signature.sign()));

        return context.getClientCertificateVerify();
    }

    @Override
    public FinishedImpl createFinished() throws Exception {
        byte[] verify_data = hkdf.hmac(
                finished_key,
                TranscriptHash.compute(ciphersuite.hash(), allMessages()));

        context.setClientFinished(factory.createFinished(verify_data));

        resumption_master_secret = hkdf.deriveSecret(
                master_secret,
                res_master,
                allMessages());
        client_application_write_key = hkdf.expandLabel(
                client_application_traffic_secret_0,
                key,
                ZERO_HASH_VALUE,
                ciphersuite.keyLength());
        client_application_write_iv = hkdf.expandLabel(
                client_application_traffic_secret_0,
                iv,
                ZERO_HASH_VALUE,
                ciphersuite.ivLength());
        server_application_write_key = hkdf.expandLabel(
                server_application_traffic_secret_0,
                key,
                ZERO_HASH_VALUE,
                ciphersuite.keyLength());
        server_application_write_iv = hkdf.expandLabel(
                server_application_traffic_secret_0,
                iv,
                ZERO_HASH_VALUE,
                ciphersuite.ivLength());

        return context.getClientFinished();
    }

    private void handleHelloRetryRequest(HelloRetryRequestImpl helloRetryRequest) {
        context.setHelloRetryRequest(helloRetryRequest);
    }

    void handleServerHello(ServerHelloImpl serverHello) throws Exception {
        KeyShareImpl.ServerHelloImpl keyShare = serverHello.findKeyShare();

        negotiator.processKeyShareEntry(keyShare.getServerShare());
        dh_shared_secret = negotiator.generateSecret();

        context.setServerHello(serverHello);
        if (!ciphersuite.equals(serverHello.getCipherSuite())) {
            throw new RuntimeException();
        }

        byte[] psk = zeroes(hkdf.getHashLength());

        HandshakeImpl wrappedClientHello = toHandshake(context.getFirstClientHello());

        early_secret = hkdf.extract(ZERO_SALT, psk);
        binder_key = hkdf.deriveSecret(
                early_secret,
                concatenate(ext_binder, res_binder));
        client_early_traffic_secret = hkdf.deriveSecret(
                early_secret,
                c_e_traffic,
                wrappedClientHello);
        early_exporter_master_secret = hkdf.deriveSecret(
                early_secret,
                e_exp_master,
                wrappedClientHello);

        handshake_secret_salt = hkdf.deriveSecret(early_secret, derived);

        HandshakeImpl wrappedServerHello = toHandshake(serverHello);

        handshake_secret = hkdf.extract(handshake_secret_salt, dh_shared_secret);
        client_handshake_traffic_secret = hkdf.deriveSecret(
                handshake_secret,
                c_hs_traffic,
                wrappedClientHello, wrappedServerHello);
        server_handshake_traffic_secret = hkdf.deriveSecret(
                handshake_secret,
                s_hs_traffic,
                wrappedClientHello, wrappedServerHello);
        master_secret = hkdf.extract(
                hkdf.deriveSecret(handshake_secret, derived),
                zeroes(hkdf.getHashLength()));

        client_handshake_write_key = hkdf.expandLabel(
                client_handshake_traffic_secret,
                key,
                ZERO_HASH_VALUE,
                ciphersuite.keyLength());
        client_handshake_write_iv = hkdf.expandLabel(
                client_handshake_traffic_secret,
                iv,
                ZERO_HASH_VALUE,
                ciphersuite.ivLength());
        server_handshake_write_key = hkdf.expandLabel(
                server_handshake_traffic_secret,
                key,
                ZERO_HASH_VALUE,
                ciphersuite.keyLength());
        server_handshake_write_iv = hkdf.expandLabel(
                server_handshake_traffic_secret,
                iv,
                ZERO_HASH_VALUE,
                ciphersuite.ivLength());
        finished_key = hkdf.expandLabel(
                client_handshake_traffic_secret,
                finished,
                ZERO_HASH_VALUE,
                hkdf.getHashLength());

        handshakeEncryptor = AEAD.createEncryptor(
                ciphersuite.cipher(),
                client_handshake_write_key, 
                client_handshake_write_iv);
        handshakeDecryptor = AEAD.createDecryptor(
                ciphersuite.cipher(),
                server_handshake_write_key, 
                server_handshake_write_iv);
    }

    private void handleCertificateRequest(CertificateRequestImpl certificateRequest) {
        certificate_request_context = certificateRequest.getCertificateRequestContext();
        context.setServerCertificateRequest(certificateRequest);
    }

    private void handleCertificate(CertificateImpl certificate) {
         context.setServerCertificate(certificate);
    }

    private void handleCertificateVerify(CertificateVerifyImpl certificateVerify) {
        context.setServerCertificateVerify(certificateVerify);
    }

    private void handleEncryptedExtensions(EncryptedExtensionsImpl encryptedExtensions) {
        context.setEncryptedExtensions(encryptedExtensions);
    }

    private void handleFinished(FinishedImpl message) throws Exception {
        byte[] verify_key = hkdf.expandLabel(
                server_handshake_traffic_secret,
                finished,
                ZERO_HASH_VALUE,
                hkdf.getHashLength());

        byte[] verify_data = hkdf.hmac(verify_key,
                TranscriptHash.compute(ciphersuite.hash(), allMessages()));

        boolean success = Arrays.equals(verify_data, message.getVerifyData());
        if (!success) {
            throw new RuntimeException();
        }

        context.setServerFinished(message);

        client_application_traffic_secret_0 = hkdf.deriveSecret(
                master_secret,
                c_ap_traffic,
                allMessages());
        server_application_traffic_secret_0 = hkdf.deriveSecret(
                master_secret,
                s_ap_traffic,
                allMessages());
        exporter_master_secret = hkdf.deriveSecret(
                master_secret,
                exp_master,
                allMessages());
    }

    @Override
    public void handle(TLSPlaintext tlsPlaintext) throws Exception {
        ContentTypeImpl type;
        byte[] content;

        if (tlsPlaintext.containsApplicationData()) {
            TLSInnerPlaintextImpl tlsInnerPlaintext = decrypt(tlsPlaintext);
            type = tlsInnerPlaintext.getType();
            content = tlsInnerPlaintext.getContent();
        } else {
            type = tlsPlaintext.getType();
            content = tlsPlaintext.getFragment();
        }

        if (ContentTypeImpl.alert.equals(type)) {
            receivedAlert = AlertImpl.parse(tlsPlaintext.getFragment());
        } else if (ContentTypeImpl.handshake.equals(type)) {
            ByteBuffer buffer = ByteBuffer.wrap(content);
            while (buffer.remaining() > 0) {
                handle(HandshakeImpl.parse(buffer));
            }
        } else {
            throw new RuntimeException();
        }
    }

    void handle(HandshakeImpl handshake) throws Exception {
        if (handshake.containsHelloRetryRequest()) {
            handleHelloRetryRequest(HelloRetryRequestImpl.parse(handshake.getBody()));
        } else if (handshake.containsServerHello()) {
            handleServerHello(ServerHelloImpl.parse(handshake.getBody()));
        } else if (handshake.containsEncryptedExtensions()) {
            handleEncryptedExtensions(EncryptedExtensionsImpl.parse(handshake.getBody()));
        } else if (handshake.containsCertificateRequest()) {
            handleCertificateRequest(CertificateRequestImpl.parse(handshake.getBody()));
        } else if (handshake.containsCertificate()) {
            handleCertificate(CertificateImpl.parse(handshake.getBody(), 
                    buf -> CertificateEntryImpl.X509.parse(buf)));
        } else if (handshake.containsCertificateVerify()) {
            handleCertificateVerify(CertificateVerifyImpl.parse(handshake.getBody()));
        } else if (handshake.containsFinished()) {
            handleFinished(FinishedImpl.parse(handshake.getBody(), ciphersuite.hashLength()));
        } else {
            throw new RuntimeException();
        }
    }

    TLSInnerPlaintextImpl decrypt(TLSPlaintext tlsPlaintext) throws Exception {
        return TLSInnerPlaintextImpl.parse(
                handshakeDecryptor.decrypt(tlsPlaintext.getFragment()));
    }

    TLSPlaintextImpl[] encrypt(HandshakeMessage message) throws Exception {
        return encrypt(toHandshake(message));
    }

    private TLSPlaintextImpl[] encrypt(HandshakeImpl message) throws Exception {
        return encrypt(factory.createTLSInnerPlaintext(ContentTypeImpl.handshake, message.encoding(), NO_PADDING));
    }

    private TLSPlaintextImpl[] encrypt(TLSInnerPlaintextImpl message) throws Exception {
        return factory.createTLSPlaintexts(ContentTypeImpl.application_data, 
                ProtocolVersionImpl.TLSv10, 
                handshakeEncryptor.encrypt(message.encoding()));
    }

    @Override
    public ApplicationDataChannel start(Connection connection) throws Exception {
        reset();

        connection.send(factory.createTLSPlaintexts(ContentTypeImpl.handshake, 
                ProtocolVersionImpl.TLSv10, 
                toHandshake(createClientHello()).encoding()));
        
        ByteBuffer buffer = ByteBuffer.wrap(connection.read());
        if (buffer.remaining() == 0) {
            throw new RuntimeException();
        }

        while (buffer.remaining() > 0) {
            TLSPlaintextImpl tlsPlaintext = TLSPlaintextImpl.parse(buffer);
            handle(tlsPlaintext);
        }
        
        if (receivedAlert()) {            
            throw new RuntimeException();
        }

        if (requestedClientAuth()) {
            connection.send(encrypt(createCertificate()));
            connection.send(encrypt(createCertificateVerify()));
        }
        
        connection.send(encrypt(createFinished()));
        
        applicationData = createApplicationDataChannel(connection);
     
        TLSInnerPlaintextImpl tlsInnerPlaintext = TLSInnerPlaintextImpl.parse(applicationData.decrypt(TLSPlaintextImpl.parse(connection.read()).getFragment()));
        
        if (tlsInnerPlaintext.containsHandshake()) {
            HandshakeImpl handshake = HandshakeImpl.parse(tlsInnerPlaintext.getContent());
            if (!handshake.containsNewSessionTicket()) {
                throw new RuntimeException();
            }
            
            // TODO: handle NewSessionTicket
        }
        
        if (tlsInnerPlaintext.containsApplicationData()) {
            throw new RuntimeException("Oops, I can't handle application data!");
        }
        
        return applicationData;
    }
    
    private boolean requestedClientAuth() {
        return certificate_request_context != null;
    }
    
    public static ClientHandshaker create(StructFactoryImpl factory, 
            SignatureSchemeImpl scheme, NamedGroupImpl group,
            Negotiator negotiator, CipherSuiteImpl ciphersuite,
            CertificateHolder clientCertificate) throws Exception {

        return new ClientHandshaker(factory, scheme, group, negotiator, ciphersuite, 
                HKDF.create(ciphersuite.hash()), clientCertificate);
    }

    private static byte[] zeroes(int length) {
        return new byte[length];
    }

}
