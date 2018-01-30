package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls.Random;
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
import com.gypsyengineer.tlsbunny.utils.CertificateHolder;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.gypsyengineer.tlsbunny.utils.Utils.concatenate;

public class ClientHandshaker {

    private static final byte[] ZERO_HASH_VALUE = new byte[0];
    private static final byte[] ZERO_SALT = new byte[0];

    private static final byte[] ext_binder      = "ext binder".getBytes();
    private static final byte[] res_binder      = "res binder".getBytes();
    private static final byte[] c_e_traffic     = "c e traffic".getBytes();
    private static final byte[] e_exp_master    = "e exp master".getBytes();
    private static final byte[] derived         = "derived".getBytes();
    private static final byte[] c_hs_traffic    = "c hs traffic".getBytes();
    private static final byte[] s_hs_traffic    = "s hs traffic".getBytes();
    private static final byte[] c_ap_traffic    = "c ap traffic".getBytes();
    private static final byte[] s_ap_traffic    = "s ap traffic".getBytes();
    private static final byte[] exp_master      = "exp master".getBytes();
    private static final byte[] res_master      = "res master".getBytes();
    private static final byte[] key             = "key".getBytes();
    private static final byte[] iv              = "iv".getBytes();
    private static final byte[] finished        = "finished".getBytes();

    private static final byte[] CERTIFICATE_VERIFY_PREFIX = new byte[64];
    static {
        for (int i=0; i<CERTIFICATE_VERIFY_PREFIX.length; i++) {
            CERTIFICATE_VERIFY_PREFIX[i] = 32;
        }
    }

    private static final byte[] CERTIFICATE_VERIFY_CONTEXT_STRING =
            "TLS 1.3, client CertificateVerify".getBytes();

    public static final long DEFAULT_SEED = 0;
    public static final long SEED = Long.getLong("tlsbunny.seed", DEFAULT_SEED);
    
    private final SignatureScheme scheme;
    private final NamedGroup group;
    private final Negotiator negotiator;
    private final CipherSuite ciphersuite;
    private final HKDF hkdf;
    private final Context context = new Context();

    private byte[] dh_shared_secret;
    private byte[] early_secret;
    private byte[] binder_key;
    private byte[] client_early_traffic_secret;
    private byte[] early_exporter_master_secret;
    private byte[] handshake_secret_salt;
    private byte[] handshake_secret;
    private byte[] client_handshake_traffic_secret;
    private byte[] server_handshake_traffic_secret;
    private byte[] master_secret;
    private byte[] client_application_traffic_secret_0;
    private byte[] server_application_traffic_secret_0;
    private byte[] exporter_master_secret;
    private byte[] resumption_master_secret;
    private byte[] client_handshake_write_key;
    private byte[] client_handshake_write_iv;
    private byte[] server_handshake_write_key;
    private byte[] server_handshake_write_iv;
    private byte[] finished_key;
    private byte[] client_application_write_key;
    private byte[] client_application_write_iv;
    private byte[] server_application_write_key;
    private byte[] server_application_write_iv;

    private final CertificateHolder clientCertificate;
    private Vector<Byte> certificate_request_context;

    private AEAD handshakeEncryptor;
    private AEAD handshakeDecryptor;
    
    private Alert receivedAlert;
    
    private ApplicationDataChannel applicationData;

    ClientHandshaker(SignatureScheme scheme, NamedGroup group,
            Negotiator negotiator, CipherSuite ciphersuite, HKDF hkdf,
            CertificateHolder clientCertificate) {

        this.scheme = scheme;
        this.group = group;
        this.negotiator = negotiator;
        this.ciphersuite = ciphersuite;
        this.hkdf = hkdf;
        this.clientCertificate = clientCertificate;
    }

    public void reset() {
        dh_shared_secret = null;
        early_secret = null;
        binder_key = null;
        client_early_traffic_secret = null;
        early_exporter_master_secret = null;
        handshake_secret_salt = null;
        handshake_secret = null;
        client_handshake_traffic_secret = null;
        server_handshake_traffic_secret = null;
        master_secret = null;
        client_application_traffic_secret_0 = null;
        server_application_traffic_secret_0 = null;
        exporter_master_secret = null;
        resumption_master_secret = null;
        client_handshake_write_key = null;
        client_handshake_write_iv = null;
        server_handshake_write_key = null;
        server_handshake_write_iv = null;
        finished_key = null;
        client_application_write_key = null;
        client_application_write_iv = null;
        server_application_write_key = null;
        server_application_write_iv = null;

        handshakeEncryptor = null;
        handshakeDecryptor = null;
        certificate_request_context = null;

        context.reset();
    }

    public void updateFirstClientHello(ClientHello clientHello) {
        context.setFirstClientHello(clientHello);
    }

    public void updateSecondClientHello(ClientHello clientHello) {
        context.setSecondClientHello(clientHello);
    }

    public void update(Certificate certificate) {
        context.setClientCertificate(certificate);
    }

    public void update(CertificateVerify certificateVerify) {
        context.setClientCertificateVerify(certificateVerify);
    }

    public void update(Finished finished) {
        context.setClientFinished(finished);
    }
    
    public boolean receivedAlert() {
        return receivedAlert != null;
    }

    public Alert getReceivedAlert() {
        return receivedAlert;
    }

    public boolean receivedHelloRetryRequest() {
        return context.getHelloRetryRequest() != null;
    }

    public HelloRetryRequest getReceivedHelloRetryRequest() {
        return context.getHelloRetryRequest();
    }

    public HandshakeMessage[] getReceivedHandshakeMessages() {
        return notNulls(new HandshakeMessage[] {
                context.getServerHello(),
                context.getHelloRetryRequest(),
                context.getEncryptedExtensions(),
                context.getServerCertificate(),
                context.getServerCertificateVerify(),
                context.getServerCertificateRequest(),
                context.getServerFinished()
        });
    }

    public HandshakeMessage[] getSentHandshakeMessages() {
        return notNulls(new HandshakeMessage[] {
                context.getFirstClientHello(),
                context.getSecondClientHello(),
                context.getClientCertificate(),
                context.getClientCertificateVerify(),
                context.getClientFinished()
        });
    }

    public ApplicationDataChannel applicationData() throws Exception {
        return applicationData;
    }

    public ClientHello createClientHello() throws Exception {
        ClientHello hello = new ClientHello();
        hello.setRandom(createRandom());
        
        hello.addCompressionMethod(CompressionMethod.createNull());
        hello.addCipherSuite(CipherSuite.TLS_AES_128_GCM_SHA256);
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

    public Certificate createCertificate() {
        context.setClientCertificate(
                new Certificate(
                    certificate_request_context, 
                    Vector.wrap(
                            Certificate.CERTIFICATE_LIST_LENGTH_BYTES, 
                            CertificateEntry.X509.wrap(clientCertificate.getCertData()))));

        return context.getClientCertificate();
    }

    public CertificateVerify createCertificateVerify() throws Exception {
        byte[] content = Utils.concatenate(
                CERTIFICATE_VERIFY_PREFIX,
                CERTIFICATE_VERIFY_CONTEXT_STRING,
                new byte[] { 0 },
                TranscriptHash.compute(ciphersuite.hash(),
                        context.allMessages()));

        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(
                KeyFactory.getInstance("EC").generatePrivate(
                        new PKCS8EncodedKeySpec(clientCertificate.getKeyData())));
        signature.update(content);

        context.setClientCertificateVerify(
                CertificateVerify.create(
                    SignatureScheme.ecdsa_secp256r1_sha256,
                    signature.sign()));

        return context.getClientCertificateVerify();
    }

    public Finished createFinished() throws Exception {
        byte[] verify_data = hkdf.hmac(
                finished_key,
                TranscriptHash.compute(ciphersuite.hash(), context.allMessages()));

        context.setClientFinished(new Finished(verify_data));

        resumption_master_secret = hkdf.deriveSecret(
                master_secret,
                res_master,
                context.allMessages());
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

    public TLSPlaintext[] createEncryptedCertificate() throws Exception {
        return encrypt(createCertificate());
    }

    public TLSPlaintext[] createEncryptedCertificateVerify() throws Exception {
        return encrypt(createCertificateVerify());
    }

    public TLSPlaintext[] createEnctyptedFinished() throws Exception {
        return encrypt(createFinished());
    }

    public void handleHelloRetryRequest(HelloRetryRequest helloRetryRequest) {
        context.setHelloRetryRequest(helloRetryRequest);
    }

    public void handleServerHello(ServerHello serverHello) throws Exception {
        KeyShare.ServerHello keyShare = serverHello.findKeyShare();

        negotiator.processKeyShareEntry(keyShare.getServerShare());
        dh_shared_secret = negotiator.generateSecret();

        context.setServerHello(serverHello);
        if (!ciphersuite.equals(serverHello.getCipherSuite())) {
            throw new RuntimeException();
        }

        byte[] psk = zeroes(hkdf.getHashLength());

        Handshake wrappedClientHello = Handshake.wrap(context.getFirstClientHello());

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

        Handshake wrappedServerHello = Handshake.wrap(serverHello);

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

    public void handleCertificateRequest(CertificateRequest certificateRequest) {
        certificate_request_context = certificateRequest.getCertificateRequestContext();
        context.setServerCertificateRequest(certificateRequest);
    }

    public void handleCertificate(Certificate certificate) {
         context.setServerCertificate(certificate);
    }

    public void handleCertificateVerify(CertificateVerify certificateVerify) {
        context.setServerCertificateVerify(certificateVerify);
    }

    public void handleEncryptedExtensions(EncryptedExtensions encryptedExtensions) {
        context.setEncryptedExtensions(encryptedExtensions);
    }

    public void handleFinished(Finished message) throws Exception {
        byte[] verify_key = hkdf.expandLabel(
                server_handshake_traffic_secret,
                finished,
                ZERO_HASH_VALUE,
                hkdf.getHashLength());

        byte[] verify_data = hkdf.hmac(verify_key,
                TranscriptHash.compute(ciphersuite.hash(), context.allMessages()));

        boolean success = Arrays.equals(verify_data, message.getVerifyData());
        if (!success) {
            throw new RuntimeException();
        }

        context.setServerFinished(message);

        client_application_traffic_secret_0 = hkdf.deriveSecret(master_secret,
                c_ap_traffic,
                context.allMessages());
        server_application_traffic_secret_0 = hkdf.deriveSecret(master_secret,
                s_ap_traffic,
                context.allMessages());
        exporter_master_secret = hkdf.deriveSecret(master_secret,
                exp_master,
                context.allMessages());
    }

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

    public void handle(Handshake handshake) throws Exception {
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

    public TLSInnerPlaintext decrypt(TLSPlaintext tlsPlaintext) throws Exception {
        return TLSInnerPlaintext.parse(
                decryptHandshakeData(tlsPlaintext.getFragment()));
    }

    public TLSPlaintext[] encrypt(HandshakeMessage message) throws Exception {
        return encrypt(Handshake.wrap(message));
    }

    public TLSPlaintext[] encrypt(Handshake message) throws Exception {
        return encrypt(TLSInnerPlaintext.noPadding(
                ContentType.handshake, message.encoding()));
    }

    public TLSPlaintext[] encrypt(TLSInnerPlaintext message) throws Exception {
        return TLSPlaintext.wrap(
                ContentType.application_data, 
                ProtocolVersion.TLSv10, 
                handshakeEncryptor.encrypt(message.encoding()));
    }
    
    public byte[] decryptHandshakeData(byte[] ciphertext) throws Exception {
        return handshakeDecryptor.decrypt(ciphertext);
    }

    public byte[] encryptHandshakeData(byte[] plaintext) throws Exception {
        return handshakeEncryptor.encrypt(plaintext);
    }

    public ApplicationDataChannel start(Connection connection) throws Exception {
        reset();

        connection.send(TLSPlaintext.wrap(
                ContentType.handshake, 
                ProtocolVersion.TLSv10, 
                Handshake.wrap(createClientHello()).encoding()));
        
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
    
    ApplicationDataChannel createApplicationDataChannel(Connection connection) 
            throws Exception {
        
        return new ApplicationDataChannel(
                connection, 
                AEAD.createEncryptor(
                    ciphersuite.cipher(),
                    client_application_write_key, 
                    client_application_write_iv), 
                AEAD.createDecryptor(
                    ciphersuite.cipher(),
                    server_application_write_key, 
                    server_application_write_iv));
    }
    
    byte[] getCurrentHash() throws Exception {
        return TranscriptHash.compute(
                ciphersuite.hash(),
                context.allMessages());
    }
    
    Context getContext() {
        return context;
    }
    
    byte[] getEarlySecret() {
        return early_secret.clone();
    }
    
    byte[] getHandshakeSecret() {
        return handshake_secret.clone();
    }
    
    byte[] getHandshakeSecretSalt() {
        return handshake_secret_salt.clone();
    }
    
    byte[] getClientHandshakeTrafficSecret() {
        return client_handshake_traffic_secret.clone();
    }
    
    byte[] getServerHandshakeTrafficSecret() {
        return server_handshake_traffic_secret.clone();
    }
    
    byte[] getMasterSecret() {
        return master_secret.clone();
    }

    byte[] getClientHandshakeWriteKey() {
        return client_handshake_write_key.clone();
    }
    
    byte[] getClientHandshakeWriteIv() {
        return client_handshake_write_iv.clone();
    }
    
    byte[] getServerHandshakeWriteKey() {
        return server_handshake_write_key.clone();
    }
    
    byte[] getServerHandshakeWriteIv() {
        return server_handshake_write_iv.clone();
    }
    
    byte[] getClientApplicationTrafficSecret0() {
        return client_application_traffic_secret_0.clone();
    }
    
    byte[] getServerApplicationTrafficSecret0() {
        return server_application_traffic_secret_0.clone();
    }
    
    byte[] getExporterMasterSecret() {
        return exporter_master_secret.clone();
    }
    
    byte[] getServerApplicationWriteKey() {
        return server_application_write_key.clone();
    }
    
    byte[] getServerApplicationWriteIv() {
        return server_application_write_iv.clone();
    }
    
    byte[] getClientApplicationWriteKey() {
        return client_application_write_key.clone();
    }
    
    byte[] getClientApplicationWriteIv() {
        return client_application_write_iv.clone();
    }
    
    byte[] getFinishedKey() {
        return finished_key.clone();
    }
    
    private boolean requestedClientAuth() {
        return certificate_request_context != null;
    }
    
    private static Random createRandom() {
        java.util.Random generator = new java.util.Random(SEED);
        byte[] random_bytes = new byte[Random.LENGTH];
        generator.nextBytes(random_bytes);
        Random random = new Random();
        random.setBytes(random_bytes);
       
        return random;
    }
    
    public static ClientHandshaker create(SignatureScheme scheme, NamedGroup group,
            Negotiator negotiator, CipherSuite ciphersuite,
            CertificateHolder clientCertificate) throws Exception {

        return new ClientHandshaker(scheme, group, negotiator, ciphersuite, 
                HKDF.create(ciphersuite.hash()), clientCertificate);
    }

    private static byte[] zeroes(int length) {
        return new byte[length];
    }

    private static HandshakeMessage[] notNulls(HandshakeMessage[] objects) {
        List<HandshakeMessage> messages = new ArrayList<>();
        for (HandshakeMessage object : objects) {
            if (object != null) {
                messages.add(object);
            }
        }

        return messages.toArray(new HandshakeMessage[messages.size()]);
    }

}
