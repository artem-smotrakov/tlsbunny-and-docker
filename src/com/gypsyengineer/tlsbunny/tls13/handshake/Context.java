package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import java.util.ArrayList;
import java.util.List;

public class Context {

    // TODO: find a better place for these constants
    // TODO: these constants shouldn't be public
    public static final byte[] ext_binder      = "ext binder".getBytes();
    public static final byte[] res_binder      = "res binder".getBytes();
    public static final byte[] c_e_traffic     = "c e traffic".getBytes();
    public static final byte[] e_exp_master    = "e exp master".getBytes();
    public static final byte[] derived         = "derived".getBytes();
    public static final byte[] c_hs_traffic    = "c hs traffic".getBytes();
    public static final byte[] s_hs_traffic    = "s hs traffic".getBytes();
    public static final byte[] c_ap_traffic    = "c ap traffic".getBytes();
    public static final byte[] s_ap_traffic    = "s ap traffic".getBytes();
    public static final byte[] exp_master      = "exp master".getBytes();
    public static final byte[] res_master      = "res master".getBytes();
    public static final byte[] key             = "key".getBytes();
    public static final byte[] iv              = "iv".getBytes();
    public static final byte[] finished        = "finished".getBytes();

    // TODO: find a better place for these constants
    public static final byte[] ZERO_SALT = new byte[0];
    public static final byte[] ZERO_HASH_VALUE = new byte[0];

    private Handshake firstClientHello;
    private Handshake helloRetryRequest;
    private Handshake secondClientHello;
    private Handshake serverHello;
    private Handshake encryptedExtensions;
    private Handshake serverCertificateRequest;
    private Handshake serverCertificate;
    private Handshake serverCertificateVerify;
    private Handshake serverFinished;
    private Handshake endOfEarlyData;
    private Handshake clientCertificate;
    private Handshake clientCertificateVerify;
    private Handshake clientFinished;

    public StructFactory factory;

    // TODO: these fields should have private or package access
    public byte[] dh_shared_secret;
    public byte[] early_secret;
    public byte[] binder_key;
    public byte[] client_early_traffic_secret;
    public byte[] early_exporter_master_secret;
    public byte[] handshake_secret_salt;
    public byte[] handshake_secret;
    public byte[] client_handshake_traffic_secret;
    public byte[] server_handshake_traffic_secret;
    public byte[] master_secret;
    public byte[] client_application_traffic_secret_0;
    public byte[] server_application_traffic_secret_0;
    public byte[] exporter_master_secret;
    public byte[] resumption_master_secret;
    public byte[] client_handshake_write_key;
    public byte[] client_handshake_write_iv;
    public byte[] server_handshake_write_key;
    public byte[] server_handshake_write_iv;
    public byte[] finished_key;
    public byte[] client_application_write_key;
    public byte[] client_application_write_iv;
    public byte[] server_application_write_key;
    public byte[] server_application_write_iv;

    public Vector<Byte> certificate_request_context;

    // TODO: these fields should not be public
    public AEAD handshakeEncryptor;
    public AEAD handshakeDecryptor;
    public AEAD applicationDataEnctyptor;
    public AEAD applicationDataDecryptor;

    public void reset() {
        firstClientHello = null;
        helloRetryRequest = null;
        secondClientHello = null;
        serverHello = null;
        encryptedExtensions = null;
        serverCertificateRequest = null;
        serverCertificate = null;
        serverCertificateVerify = null;
        serverFinished = null;
        endOfEarlyData = null;
        clientCertificate = null;
        clientCertificateVerify = null;
        clientFinished = null;

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

        certificate_request_context = null;

        handshakeEncryptor = null;
        handshakeDecryptor = null;
    }

    public boolean hasFirstClientHello() {
        return firstClientHello != null;
    }

    public boolean hasSecondClientHello() {
        return secondClientHello != null;
    }

    public Handshake getFirstClientHello() {
        return firstClientHello;
    }

    public void setFirstClientHello(Handshake firstClientHello) {
        this.firstClientHello = firstClientHello;
    }

    public Handshake getHelloRetryRequest() {
        return helloRetryRequest;
    }

    public void setHelloRetryRequest(Handshake helloRetryRequest) {
        this.helloRetryRequest = helloRetryRequest;
    }

    public Handshake getSecondClientHello() {
        return secondClientHello;
    }

    public void setSecondClientHello(Handshake secondClientHello) {
        this.secondClientHello = secondClientHello;
    }

    public Handshake getServerHello() {
        return serverHello;
    }

    public void setServerHello(Handshake serverHello) {
        this.serverHello = serverHello;
    }

    public Handshake getEncryptedExtensions() {
        return encryptedExtensions;
    }

    public void setEncryptedExtensions(Handshake encryptedExtensions) {
        this.encryptedExtensions = encryptedExtensions;
    }

    public Handshake getServerCertificateRequest() {
        return serverCertificateRequest;
    }

    public void setServerCertificateRequest(Handshake serverCertificateRequest) {
        this.serverCertificateRequest = serverCertificateRequest;
    }

    public Handshake getServerCertificate() {
        return serverCertificate;
    }

    public void setServerCertificate(Handshake serverCertificate) {
        this.serverCertificate = serverCertificate;
    }

    public Handshake getServerCertificateVerify() {
        return serverCertificateVerify;
    }

    public void setServerCertificateVerify(Handshake serverCertificateVerify) {
        this.serverCertificateVerify = serverCertificateVerify;
    }

    public Handshake getServerFinished() {
        return serverFinished;
    }

    public void setServerFinished(Handshake serverFinished) {
        this.serverFinished = serverFinished;
    }

    public Handshake getEndOfEarlyData() {
        return endOfEarlyData;
    }

    public void setEndOfEarlyData(Handshake endOfEarlyData) {
        this.endOfEarlyData = endOfEarlyData;
    }

    public Handshake getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(Handshake clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public Handshake getClientCertificateVerify() {
        return clientCertificateVerify;
    }

    public void setClientCertificateVerify(Handshake clientCertificateVerify) {
        this.clientCertificateVerify = clientCertificateVerify;
    }

    public Handshake getClientFinished() {
        return clientFinished;
    }

    public void setClientFinished(Handshake clientFinished) {
        this.clientFinished = clientFinished;
    }

    public Handshake[] allMessages() {
        Handshake[] messages = new Handshake[] {
                firstClientHello,
                helloRetryRequest,
                secondClientHello,
                serverHello,
                encryptedExtensions,
                serverCertificateRequest,
                serverCertificate,
                serverCertificateVerify,
                serverFinished,
                endOfEarlyData,
                clientCertificate,
                clientCertificateVerify,
                clientFinished
        };

        List<Handshake> list = new ArrayList<>();
        for (Handshake message : messages) {
            if (message != null) {
                list.add(message);
            }
        }

        return list.toArray(new Handshake[list.size()]);
    }

}
