package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.Certificate;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateVerify;
import com.gypsyengineer.tlsbunny.tls13.struct.ClientHello;
import com.gypsyengineer.tlsbunny.tls13.struct.EncryptedExtensions;
import com.gypsyengineer.tlsbunny.tls13.struct.EndOfEarlyData;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeMessage;
import com.gypsyengineer.tlsbunny.tls13.struct.HelloRetryRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Context {

    private ClientHello             firstClientHello;
    private HelloRetryRequest       helloRetryRequest;
    private ClientHello             secondClientHello;
    private ServerHello             serverHello;
    private EncryptedExtensions     encryptedExtensions;
    private CertificateRequest      serverCertificateRequest;
    private Certificate             serverCertificate;
    private CertificateVerify       serverCertificateVerify;
    private Finished                serverFinished;
    private EndOfEarlyData          endOfEarlyData;
    private Certificate             clientCertificate;
    private CertificateVerify       clientCertificateVerify;
    private Finished                clientFinished;

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

    public ClientHello getFirstClientHello() {
        return firstClientHello;
    }

    public void setFirstClientHello(ClientHello firstClientHello) {
        this.firstClientHello = firstClientHello;
    }

    public HelloRetryRequest getHelloRetryRequest() {
        return helloRetryRequest;
    }

    public void setHelloRetryRequest(HelloRetryRequest helloRetryRequest) {
        this.helloRetryRequest = helloRetryRequest;
    }

    public ClientHello getSecondClientHello() {
        return secondClientHello;
    }

    public void setSecondClientHello(ClientHello secondClientHello) {
        this.secondClientHello = secondClientHello;
    }

    public ServerHello getServerHello() {
        return serverHello;
    }

    public void setServerHello(ServerHello serverHello) {
        this.serverHello = serverHello;
    }

    public EncryptedExtensions getEncryptedExtensions() {
        return encryptedExtensions;
    }

    public void setEncryptedExtensions(EncryptedExtensions encryptedExtensions) {
        this.encryptedExtensions = encryptedExtensions;
    }

    public CertificateRequest getServerCertificateRequest() {
        return serverCertificateRequest;
    }

    public void setServerCertificateRequest(CertificateRequest serverCertificateRequest) {
        this.serverCertificateRequest = serverCertificateRequest;
    }

    public Certificate getServerCertificate() {
        return serverCertificate;
    }

    public void setServerCertificate(Certificate serverCertificate) {
        this.serverCertificate = serverCertificate;
    }

    public CertificateVerify getServerCertificateVerify() {
        return serverCertificateVerify;
    }

    public void setServerCertificateVerify(CertificateVerify serverCertificateVerify) {
        this.serverCertificateVerify = serverCertificateVerify;
    }

    public Finished getServerFinished() {
        return serverFinished;
    }

    public void setServerFinished(Finished serverFinished) {
        this.serverFinished = serverFinished;
    }

    public EndOfEarlyData getEndOfEarlyData() {
        return endOfEarlyData;
    }

    public void setEndOfEarlyData(EndOfEarlyData endOfEarlyData) {
        this.endOfEarlyData = endOfEarlyData;
    }

    public Certificate getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(Certificate clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public CertificateVerify getClientCertificateVerify() {
        return clientCertificateVerify;
    }

    public void setClientCertificateVerify(CertificateVerify clientCertificateVerify) {
        this.clientCertificateVerify = clientCertificateVerify;
    }

    public Finished getClientFinished() {
        return clientFinished;
    }

    public void setClientFinished(Finished clientFinished) {
        this.clientFinished = clientFinished;
    }

    public Handshake[] allMessages() throws IOException {
        HandshakeMessage[] messages = new HandshakeMessage[] {
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
        for (HandshakeMessage message : messages) {
            if (message != null) {
                list.add(Handshake.wrap(message));
            }
        }

        return list.toArray(new Handshake[list.size()]);
    }
}
