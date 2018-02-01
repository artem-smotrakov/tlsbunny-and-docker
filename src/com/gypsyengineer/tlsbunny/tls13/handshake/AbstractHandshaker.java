package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.crypto.TranscriptHash;
import com.gypsyengineer.tlsbunny.tls13.struct.Alert;
import com.gypsyengineer.tlsbunny.tls13.struct.Certificate;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateVerify;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.ClientHello;
import com.gypsyengineer.tlsbunny.tls13.struct.EncryptedExtensions;
import com.gypsyengineer.tlsbunny.tls13.struct.EndOfEarlyData;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeMessage;
import com.gypsyengineer.tlsbunny.tls13.struct.HelloRetryRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.Connection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHandshaker implements Handshaker {

    static final byte[] ZERO_HASH_VALUE = new byte[0];
    static final byte[] ZERO_SALT = new byte[0];

    static final byte[] ext_binder      = "ext binder".getBytes();
    static final byte[] res_binder      = "res binder".getBytes();
    static final byte[] c_e_traffic     = "c e traffic".getBytes();
    static final byte[] e_exp_master    = "e exp master".getBytes();
    static final byte[] derived         = "derived".getBytes();
    static final byte[] c_hs_traffic    = "c hs traffic".getBytes();
    static final byte[] s_hs_traffic    = "s hs traffic".getBytes();
    static final byte[] c_ap_traffic    = "c ap traffic".getBytes();
    static final byte[] s_ap_traffic    = "s ap traffic".getBytes();
    static final byte[] exp_master      = "exp master".getBytes();
    static final byte[] res_master      = "res master".getBytes();
    static final byte[] key             = "key".getBytes();
    static final byte[] iv              = "iv".getBytes();
    static final byte[] finished        = "finished".getBytes();

    static final byte[] CERTIFICATE_VERIFY_PREFIX = new byte[64];
    static {
        for (int i=0; i<CERTIFICATE_VERIFY_PREFIX.length; i++) {
            CERTIFICATE_VERIFY_PREFIX[i] = 32;
        }
    }

    static final byte[] CERTIFICATE_VERIFY_CONTEXT_STRING =
            "TLS 1.3, client CertificateVerify".getBytes();

    static final long DEFAULT_SEED = 0;
    static final long SEED = Long.getLong("tlsbunny.seed", DEFAULT_SEED);
    
    final StructFactory factory;
    final SignatureScheme scheme;
    final NamedGroup group;
    final Negotiator negotiator;
    final CipherSuite ciphersuite;
    final HKDF hkdf;
    final Context context = new Context();
    // TODO: add TranscriptHash field
    
    byte[] dh_shared_secret;
    byte[] early_secret;
    byte[] binder_key;
    byte[] client_early_traffic_secret;
    byte[] early_exporter_master_secret;
    byte[] handshake_secret_salt;
    byte[] handshake_secret;
    byte[] client_handshake_traffic_secret;
    byte[] server_handshake_traffic_secret;
    byte[] master_secret;
    byte[] client_application_traffic_secret_0;
    byte[] server_application_traffic_secret_0;
    byte[] exporter_master_secret;
    byte[] resumption_master_secret;
    byte[] client_handshake_write_key;
    byte[] client_handshake_write_iv;
    byte[] server_handshake_write_key;
    byte[] server_handshake_write_iv;
    byte[] finished_key;
    byte[] client_application_write_key;
    byte[] client_application_write_iv;
    byte[] server_application_write_key;
    byte[] server_application_write_iv;
    
    AEAD handshakeEncryptor;
    AEAD handshakeDecryptor;
    
    Alert receivedAlert;
    
    ApplicationDataChannel applicationData;

    AbstractHandshaker(StructFactory factory, SignatureScheme scheme, NamedGroup group, 
            Negotiator negotiator, CipherSuite ciphersuite, HKDF hkdf) {
        
        this.factory = factory;
        this.scheme = scheme;
        this.group = group;
        this.negotiator = negotiator;
        this.ciphersuite = ciphersuite;
        this.hkdf = hkdf;
    }
    
    @Override
    public ClientHello createClientHello() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServerHello createServerHello() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public HelloRetryRequest createHelloRetryRequest() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public EncryptedExtensions createEncryptedExtensions() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public CertificateRequest createCertificateRequest() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public Certificate createCertificate() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public CertificateVerify createCertificateVerify() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public EndOfEarlyData createEndOfEarlyData() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public Finished createFinished() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public void handle(TLSPlaintext tlsPlaintext) throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public ApplicationDataChannel start(Connection connection) throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public ApplicationDataChannel applicationData() throws Exception {
        return applicationData;
    }

    @Override
    public boolean receivedAlert() {
        return receivedAlert != null;
    }

    @Override
    public Alert getReceivedAlert() {
        return receivedAlert;
    }

    @Override
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
        
        applicationData = null;

        context.reset();
    }
    
    ApplicationDataChannel createApplicationDataChannel(Connection connection) 
            throws Exception {
        
        return new ApplicationDataChannel(
                factory,
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
    
    Handshake[] allMessages() throws IOException {
        List<Handshake> list = new ArrayList<>();
        for (HandshakeMessage message : context.allMessages()) {
            list.add(toHandshake(message));
        }
        
        return list.toArray(new Handshake[list.size()]);
    }
    
    Handshake toHandshake(HandshakeMessage message) throws IOException {
        return factory.createHandshake(message.type(), message.encoding());
    }
    
    byte[] getCurrentHash() throws Exception {
        return TranscriptHash.compute(
                ciphersuite.hash(),
                allMessages());
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
    
    static Random createRandom() {
        java.util.Random generator = new java.util.Random(SEED);
        byte[] random_bytes = new byte[Random.LENGTH];
        generator.nextBytes(random_bytes);
        Random random = new Random();
        random.setBytes(random_bytes);
       
        return random;
    }

}
