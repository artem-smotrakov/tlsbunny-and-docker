package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.Alert;
import com.gypsyengineer.tlsbunny.tls13.struct.Certificate;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateVerify;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.ClientHello;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShare;
import com.gypsyengineer.tlsbunny.tls13.struct.CompressionMethod;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.EncryptedExtensions;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeMessage;
import com.gypsyengineer.tlsbunny.tls13.struct.HelloRetryRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroupList;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureSchemeList;
import com.gypsyengineer.tlsbunny.tls13.struct.SupportedVersions;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.crypto.TranscriptHash;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import static com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext.NO_PADDING;
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

public class ClientHandshaker extends AbstractHandshaker {

    private final CertificateHolder clientCertificate;
    private Vector<Byte> certificate_request_context;

    ClientHandshaker(StructFactory factory, SignatureScheme scheme, NamedGroup group,
            Negotiator negotiator, CipherSuite ciphersuite, HKDF hkdf,
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
    public ClientHello createClientHello() throws Exception {
        ClientHello hello = factory.createClientHello(
                ProtocolVersion.TLSv12, 
                createRandom(), 
                StructFactory.EMPTY_SESSION_ID, 
                List.of(CipherSuite.TLS_AES_128_GCM_SHA256), 
                List.of(CompressionMethod.createNull()), 
                StructFactory.NO_EXTENSIONS);
        
        hello.addExtension(Extension.wrap(
                SupportedVersions.ClientHello.create(ProtocolVersion.TLSv13)));
        hello.addExtension(Extension.wrap(SignatureSchemeList.create(scheme)));
        hello.addExtension(Extension.wrap(NamedGroupList.create(group)));
        hello.addExtension(Extension.wrap(
                KeyShare.ClientHello.create(negotiator.createKeyShareEntry())));

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
    public Certificate createCertificate() throws IOException {
        context.setClientCertificate(
                factory.createCertificate(
                    certificate_request_context.bytes(), 
                    CertificateEntry.X509.wrap(clientCertificate.getCertData())));

        return context.getClientCertificate();
    }

    @Override
    public CertificateVerify createCertificateVerify() throws Exception {
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

        context.setClientCertificateVerify(
                factory.createCertificateVerify(
                    SignatureScheme.ecdsa_secp256r1_sha256,
                    signature.sign()));

        return context.getClientCertificateVerify();
    }

    @Override
    public Finished createFinished() throws Exception {
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

    private TLSPlaintext[] createEnctyptedFinished() throws Exception {
        return encrypt(createFinished());
    }

    private void handleHelloRetryRequest(HelloRetryRequest helloRetryRequest) {
        context.setHelloRetryRequest(helloRetryRequest);
    }

    void handleServerHello(ServerHello serverHello) throws Exception {
        KeyShare.ServerHello keyShare = serverHello.findKeyShare();

        negotiator.processKeyShareEntry(keyShare.getServerShare());
        dh_shared_secret = negotiator.generateSecret();

        context.setServerHello(serverHello);
        if (!ciphersuite.equals(serverHello.getCipherSuite())) {
            throw new RuntimeException();
        }

        byte[] psk = zeroes(hkdf.getHashLength());

        Handshake wrappedClientHello = toHandshake(context.getFirstClientHello());

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

        Handshake wrappedServerHello = toHandshake(serverHello);

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

    private void handleCertificateRequest(CertificateRequest certificateRequest) {
        certificate_request_context = certificateRequest.getCertificateRequestContext();
        context.setServerCertificateRequest(certificateRequest);
    }

    private void handleCertificate(Certificate certificate) {
         context.setServerCertificate(certificate);
    }

    private void handleCertificateVerify(CertificateVerify certificateVerify) {
        context.setServerCertificateVerify(certificateVerify);
    }

    private void handleEncryptedExtensions(EncryptedExtensions encryptedExtensions) {
        context.setEncryptedExtensions(encryptedExtensions);
    }

    private void handleFinished(Finished message) throws Exception {
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
        ContentType type;
        byte[] content;

        if (tlsPlaintext.containsApplicationData()) {
            TLSInnerPlaintext tlsInnerPlaintext = decrypt(tlsPlaintext);
            type = tlsInnerPlaintext.getType();
            content = tlsInnerPlaintext.getContent();
        } else {
            type = tlsPlaintext.getType();
            content = tlsPlaintext.getFragment();
        }

        if (ContentType.alert.equals(type)) {
            receivedAlert = Alert.parse(tlsPlaintext.getFragment());
        } else if (ContentType.handshake.equals(type)) {
            ByteBuffer buffer = ByteBuffer.wrap(content);
            while (buffer.remaining() > 0) {
                handle(Handshake.parse(buffer));
            }
        } else {
            throw new RuntimeException();
        }
    }

    void handle(Handshake handshake) throws Exception {
        if (handshake.containsHelloRetryRequest()) {
            handleHelloRetryRequest(HelloRetryRequest.parse(handshake.getBody()));
        } else if (handshake.containsServerHello()) {
            handleServerHello(ServerHello.parse(handshake.getBody()));
        } else if (handshake.containsEncryptedExtensions()) {
            handleEncryptedExtensions(EncryptedExtensions.parse(handshake.getBody()));
        } else if (handshake.containsCertificateRequest()) {
            handleCertificateRequest(CertificateRequest.parse(handshake.getBody()));
        } else if (handshake.containsCertificate()) {
            handleCertificate(Certificate.parse(
                    handshake.getBody(), 
                    buf -> CertificateEntry.X509.parse(buf)));
        } else if (handshake.containsCertificateVerify()) {
            handleCertificateVerify(CertificateVerify.parse(handshake.getBody()));
        } else if (handshake.containsFinished()) {
            handleFinished(Finished.parse(handshake.getBody(), ciphersuite.hashLength()));
        } else {
            throw new RuntimeException();
        }
    }

    TLSInnerPlaintext decrypt(TLSPlaintext tlsPlaintext) throws Exception {
        return TLSInnerPlaintext.parse(
                handshakeDecryptor.decrypt(tlsPlaintext.getFragment()));
    }

    TLSPlaintext[] encrypt(HandshakeMessage message) throws Exception {
        return encrypt(toHandshake(message));
    }

    private TLSPlaintext[] encrypt(Handshake message) throws Exception {
        return encrypt(factory.createTLSInnerPlaintext(
                ContentType.handshake, message.encoding(), NO_PADDING));
    }

    private TLSPlaintext[] encrypt(TLSInnerPlaintext message) throws Exception {
        return factory.createTLSPlaintexts(
                ContentType.application_data, 
                ProtocolVersion.TLSv10, 
                handshakeEncryptor.encrypt(message.encoding()));
    }

    @Override
    public ApplicationDataChannel start(Connection connection) throws Exception {
        reset();

        connection.send(factory.createTLSPlaintexts(
                ContentType.handshake, 
                ProtocolVersion.TLSv10, 
                toHandshake(createClientHello()).encoding()));
        
        ByteBuffer buffer = ByteBuffer.wrap(connection.read());
        if (buffer.remaining() == 0) {
            throw new RuntimeException();
        }

        while (buffer.remaining() > 0) {
            TLSPlaintext tlsPlaintext = TLSPlaintext.parse(buffer);
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
     
        TLSInnerPlaintext tlsInnerPlaintext = TLSInnerPlaintext.parse(
                applicationData.decrypt(
                        TLSPlaintext.parse(connection.read()).getFragment()));
        
        if (tlsInnerPlaintext.containsHandshake()) {
            Handshake handshake = Handshake.parse(tlsInnerPlaintext.getContent());
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
    
    public static ClientHandshaker create(StructFactory factory, 
            SignatureScheme scheme, NamedGroup group,
            Negotiator negotiator, CipherSuite ciphersuite,
            CertificateHolder clientCertificate) throws Exception {

        return new ClientHandshaker(factory, scheme, group, negotiator, ciphersuite, 
                HKDF.create(ciphersuite.hash()), clientCertificate);
    }

    private static byte[] zeroes(int length) {
        return new byte[length];
    }

}
