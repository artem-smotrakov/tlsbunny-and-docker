package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.AlertImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateRequestImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateVerifyImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ClientHelloImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ContentTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.EncryptedExtensionsImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.EndOfEarlyDataImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.FinishedImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HelloRetryRequestImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ServerHelloImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.TLSInnerPlaintextImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.TLSPlaintextImpl;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeMessage;

public interface Handshaker {
    
    ClientHelloImpl createClientHello() throws Exception;
    ServerHelloImpl createServerHello() throws Exception;
    HelloRetryRequestImpl createHelloRetryRequest() throws Exception;
    EncryptedExtensionsImpl createEncryptedExtensions() throws Exception;
    CertificateRequestImpl createCertificateRequest() throws Exception;
    CertificateImpl createCertificate() throws Exception;
    CertificateVerifyImpl createCertificateVerify() throws Exception;
    EndOfEarlyDataImpl createEndOfEarlyData() throws Exception;
    FinishedImpl createFinished() throws Exception;
    void handle(TLSPlaintext tlsPlaintext) throws Exception;
    ApplicationDataChannel start(Connection connection) throws Exception;    
    ApplicationDataChannel applicationData() throws Exception;
    void reset();
    
    boolean receivedAlert();
    AlertImpl getReceivedAlert();
}
