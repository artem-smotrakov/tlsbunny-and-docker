package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.struct.Alert;
import com.gypsyengineer.tlsbunny.tls13.struct.Certificate;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateVerify;
import com.gypsyengineer.tlsbunny.tls13.struct.ClientHello;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.EncryptedExtensions;
import com.gypsyengineer.tlsbunny.tls13.struct.EndOfEarlyData;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeMessage;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.HelloRetryRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.tls.Struct;

public interface Handshaker {
    
    ClientHello createClientHello() throws Exception;
    ServerHello createServerHello() throws Exception;
    HelloRetryRequest createHelloRetryRequest() throws Exception;
    EncryptedExtensions createEncryptedExtensions() throws Exception;
    CertificateRequest createCertificateRequest() throws Exception;
    Certificate createCertificate() throws Exception;
    CertificateVerify createCertificateVerify() throws Exception;
    EndOfEarlyData createEndOfEarlyData() throws Exception;
    Finished createFinished() throws Exception;
    void handle(TLSPlaintext tlsPlaintext) throws Exception;
    ApplicationDataChannel start(Connection connection) throws Exception;    
    ApplicationDataChannel applicationData() throws Exception;
    void reset();
    
    boolean receivedAlert();
    Alert getReceivedAlert();
}
