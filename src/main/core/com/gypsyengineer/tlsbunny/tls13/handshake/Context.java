package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.util.ArrayList;
import java.util.List;

public class Context {

    public enum Element {
        first_client_hello,
        hello_retry_request,
        second_client_hello,
        server_hello,
        encrypted_extensions,
        server_certificate_request,
        server_certificate,
        server_certificate_verify,
        server_finished,
        end_of_early_data,
        client_certificate,
        client_certificate_verify,
        client_finished
    }

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

    private boolean clientFinishedVerified = false;
    private boolean serverFinishedVerified = false;

    public StructFactory factory;
    public SignatureScheme scheme;

    // TODO: group doesn't look necessary, we can rely on negotiator
    public NamedGroup group;

    public CipherSuite suite;
    public Negotiator negotiator;
    public HKDF hkdf;

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

    private Alert alert;

    // TODO: these fields should not be public
    public AEAD handshakeEncryptor;
    public AEAD handshakeDecryptor;
    public AEAD applicationDataEncryptor;
    public AEAD applicationDataDecryptor;

    public List<byte[]> applicationData = new ArrayList<>();

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

    public void setHelloRetryRequest(Handshake helloRetryRequest) {
        this.helloRetryRequest = helloRetryRequest;
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

    public boolean hasServerHello() {
        return serverHello != null;
    }

    public void setEncryptedExtensions(Handshake encryptedExtensions) {
        this.encryptedExtensions = encryptedExtensions;
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

    public void setServerCertificateVerify(Handshake serverCertificateVerify) {
        this.serverCertificateVerify = serverCertificateVerify;
    }

    public void setServerFinished(Handshake serverFinished) {
        this.serverFinished = serverFinished;
    }

    public boolean receivedServerFinished() {
        return serverFinished != null;
    }

    public boolean receivedClientFinished() {
        return clientFinished != null;
    }

    public void verifyClientFinished() {
        clientFinishedVerified = true;
    }

    public void verifyServerFinished() {
        serverFinishedVerified = true;
    }

    public void setEndOfEarlyData(Handshake endOfEarlyData) {
        this.endOfEarlyData = endOfEarlyData;
    }

    public void setClientCertificate(Handshake clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public void setClientCertificateVerify(Handshake clientCertificateVerify) {
        this.clientCertificateVerify = clientCertificateVerify;
    }

    public void setClientFinished(Handshake clientFinished) {
        this.clientFinished = clientFinished;
    }

    public void set(Element element, Handshake message) {
        switch (element) {
            case first_client_hello:
                setFirstClientHello(message);
                break;
            case hello_retry_request:
                setHelloRetryRequest(message);
                break;
            case second_client_hello:
                setSecondClientHello(message);
                break;
            case server_hello:
                setServerHello(message);
                break;
            case encrypted_extensions:
                setEncryptedExtensions(message);
                break;
            case server_certificate_request:
                setServerCertificateRequest(message);
                break;
            case server_certificate:
                setServerCertificate(message);
                break;
            case server_certificate_verify:
                setServerCertificateVerify(message);
                break;
            case server_finished:
                setServerFinished(message);
                break;
            case end_of_early_data:
                setEndOfEarlyData(message);
                break;
            case client_certificate:
                setClientCertificate(message);
                break;
            case client_certificate_verify:
                setClientCertificateVerify(message);
                break;
            case client_finished:
                setClientFinished(message);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Handshake[] messagesForApplicationKeys() {
        return noNulls(new Handshake[] {
                firstClientHello,
                helloRetryRequest,
                secondClientHello,
                serverHello,
                encryptedExtensions,
                serverCertificateRequest,
                serverCertificate,
                serverCertificateVerify,
                serverFinishedVerified ? serverFinished : null,
                endOfEarlyData,
                clientFinishedVerified ? clientFinished : null,
        });
    }

    public Handshake[] allMessages() {
        return noNulls(new Handshake[] {
                firstClientHello,
                helloRetryRequest,
                secondClientHello,
                serverHello,
                encryptedExtensions,
                serverCertificateRequest,
                serverCertificate,
                serverCertificateVerify,
                serverFinishedVerified ? serverFinished : null,
                endOfEarlyData,
                clientCertificate,
                clientCertificateVerify,
                clientFinishedVerified ? clientFinished : null,
        });
    }

    public boolean hasAlert() {
        return alert != null;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Alert getAlert() {
        return alert;
    }

    public void addApplicationData(byte[] data) {
        applicationData.add(data);
    }

    public boolean receivedApplicationData() {
        return !applicationData.isEmpty();
    }

    private static Handshake[] noNulls(Handshake[] messages) {
        List<Handshake> list = new ArrayList<>();
        for (Handshake message : messages) {
            if (message != null) {
                list.add(message);
            }
        }

        return list.toArray(new Handshake[0]);
    }

}
