package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateRequestImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateVerifyImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ClientHelloImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.EncryptedExtensionsImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.EndOfEarlyDataImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.FinishedImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HelloRetryRequestImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ServerHelloImpl;
import java.util.ArrayList;
import java.util.List;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeMessage;

public class Context {

    private ClientHelloImpl             firstClientHello;
    private HelloRetryRequestImpl       helloRetryRequest;
    private ClientHelloImpl             secondClientHello;
    private ServerHelloImpl             serverHello;
    private EncryptedExtensionsImpl     encryptedExtensions;
    private CertificateRequestImpl      serverCertificateRequest;
    private CertificateImpl             serverCertificate;
    private CertificateVerifyImpl       serverCertificateVerify;
    private FinishedImpl                serverFinished;
    private EndOfEarlyDataImpl          endOfEarlyData;
    private CertificateImpl             clientCertificate;
    private CertificateVerifyImpl       clientCertificateVerify;
    private FinishedImpl                clientFinished;

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

    public ClientHelloImpl getFirstClientHello() {
        return firstClientHello;
    }

    public void setFirstClientHello(ClientHelloImpl firstClientHello) {
        this.firstClientHello = firstClientHello;
    }

    public HelloRetryRequestImpl getHelloRetryRequest() {
        return helloRetryRequest;
    }

    public void setHelloRetryRequest(HelloRetryRequestImpl helloRetryRequest) {
        this.helloRetryRequest = helloRetryRequest;
    }

    public ClientHelloImpl getSecondClientHello() {
        return secondClientHello;
    }

    public void setSecondClientHello(ClientHelloImpl secondClientHello) {
        this.secondClientHello = secondClientHello;
    }

    public ServerHelloImpl getServerHello() {
        return serverHello;
    }

    public void setServerHello(ServerHelloImpl serverHello) {
        this.serverHello = serverHello;
    }

    public EncryptedExtensionsImpl getEncryptedExtensions() {
        return encryptedExtensions;
    }

    public void setEncryptedExtensions(EncryptedExtensionsImpl encryptedExtensions) {
        this.encryptedExtensions = encryptedExtensions;
    }

    public CertificateRequestImpl getServerCertificateRequest() {
        return serverCertificateRequest;
    }

    public void setServerCertificateRequest(CertificateRequestImpl serverCertificateRequest) {
        this.serverCertificateRequest = serverCertificateRequest;
    }

    public CertificateImpl getServerCertificate() {
        return serverCertificate;
    }

    public void setServerCertificate(CertificateImpl serverCertificate) {
        this.serverCertificate = serverCertificate;
    }

    public CertificateVerifyImpl getServerCertificateVerify() {
        return serverCertificateVerify;
    }

    public void setServerCertificateVerify(CertificateVerifyImpl serverCertificateVerify) {
        this.serverCertificateVerify = serverCertificateVerify;
    }

    public FinishedImpl getServerFinished() {
        return serverFinished;
    }

    public void setServerFinished(FinishedImpl serverFinished) {
        this.serverFinished = serverFinished;
    }

    public EndOfEarlyDataImpl getEndOfEarlyData() {
        return endOfEarlyData;
    }

    public void setEndOfEarlyData(EndOfEarlyDataImpl endOfEarlyData) {
        this.endOfEarlyData = endOfEarlyData;
    }

    public CertificateImpl getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(CertificateImpl clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public CertificateVerifyImpl getClientCertificateVerify() {
        return clientCertificateVerify;
    }

    public void setClientCertificateVerify(CertificateVerifyImpl clientCertificateVerify) {
        this.clientCertificateVerify = clientCertificateVerify;
    }

    public FinishedImpl getClientFinished() {
        return clientFinished;
    }

    public void setClientFinished(FinishedImpl clientFinished) {
        this.clientFinished = clientFinished;
    }

    public HandshakeMessage[] allMessages() {
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

        List<HandshakeMessage> list = new ArrayList<>();
        for (HandshakeMessage message : messages) {
            if (message != null) {
                list.add(message);
            }
        }

        return list.toArray(new HandshakeMessage[list.size()]);
    }
}
