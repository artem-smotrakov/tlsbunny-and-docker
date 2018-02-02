package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import java.util.ArrayList;
import java.util.List;

public class Context {

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
