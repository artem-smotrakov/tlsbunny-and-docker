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
import com.gypsyengineer.tlsbunny.tls13.struct.StructParser;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.Connection;
import java.io.IOException;

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

    static final ApplicationDataChannel NO_APPLICATION_DATA_CHANNEL = null;

    final StructFactory factory;
    final StructParser parser;

    final SignatureScheme scheme;
    final NamedGroup group;
    final Negotiator negotiator;
    final CipherSuite ciphersuite;
    final HKDF hkdf;
    final Context context = new Context();
    // TODO: add TranscriptHash field

    Alert receivedAlert;

    ApplicationDataChannel applicationData;

    AbstractHandshaker(StructFactory factory, SignatureScheme scheme, NamedGroup group,
            Negotiator negotiator, CipherSuite ciphersuite, HKDF hkdf) {

        this.factory = factory;
        this.parser = factory.parser();

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
                    context.client_application_write_key,
                    context.client_application_write_iv),
                AEAD.createDecryptor(
                    ciphersuite.cipher(),
                    context.server_application_write_key,
                    context.server_application_write_iv));
    }

    Handshake toHandshake(HandshakeMessage message) throws IOException {
        return factory.createHandshake(message.type(), message.encoding());
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
        return context.early_secret.clone();
    }

    byte[] getHandshakeSecret() {
        return context.handshake_secret.clone();
    }

    byte[] getHandshakeSecretSalt() {
        return context.handshake_secret_salt.clone();
    }

    byte[] getClientHandshakeTrafficSecret() {
        return context.client_handshake_traffic_secret.clone();
    }

    byte[] getServerHandshakeTrafficSecret() {
        return context.server_handshake_traffic_secret.clone();
    }

    byte[] getMasterSecret() {
        return context.master_secret.clone();
    }

    byte[] getClientHandshakeWriteKey() {
        return context.client_handshake_write_key.clone();
    }

    byte[] getClientHandshakeWriteIv() {
        return context.client_handshake_write_iv.clone();
    }

    byte[] getServerHandshakeWriteKey() {
        return context.server_handshake_write_key.clone();
    }

    byte[] getServerHandshakeWriteIv() {
        return context.server_handshake_write_iv.clone();
    }

    byte[] getClientApplicationTrafficSecret0() {
        return context.client_application_traffic_secret_0.clone();
    }

    byte[] getServerApplicationTrafficSecret0() {
        return context.server_application_traffic_secret_0.clone();
    }

    byte[] getExporterMasterSecret() {
        return context.exporter_master_secret.clone();
    }

    byte[] getServerApplicationWriteKey() {
        return context.server_application_write_key.clone();
    }

    byte[] getServerApplicationWriteIv() {
        return context.server_application_write_iv.clone();
    }

    byte[] getClientApplicationWriteKey() {
        return context.client_application_write_key.clone();
    }

    byte[] getClientApplicationWriteIv() {
        return context.client_application_write_iv.clone();
    }

    byte[] getFinishedKey() {
        return context.finished_key.clone();
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
