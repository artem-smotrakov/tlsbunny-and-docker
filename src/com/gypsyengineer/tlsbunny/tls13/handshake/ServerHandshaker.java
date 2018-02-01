package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateRequestImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CertificateVerifyImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CipherSuiteImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.EncryptedExtensionsImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.FinishedImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HelloRetryRequestImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ServerHelloImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.StructFactoryImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.TLSPlaintextImpl;
import com.gypsyengineer.tlsbunny.utils.Connection;

public class ServerHandshaker extends AbstractHandshaker {

    public ServerHandshaker(StructFactoryImpl factory,
            SignatureSchemeImpl scheme, NamedGroupImpl group, 
            Negotiator negotiator, CipherSuiteImpl ciphersuite, HKDF hkdf) {
        
        super(factory, scheme, group, negotiator, ciphersuite, hkdf);
    }
    
    @Override
    public ServerHelloImpl createServerHello() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public HelloRetryRequestImpl createHelloRetryRequest() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public EncryptedExtensionsImpl createEncryptedExtensions() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public CertificateRequestImpl createCertificateRequest() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public CertificateImpl createCertificate() {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public CertificateVerifyImpl createCertificateVerify() throws Exception {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public FinishedImpl createFinished() throws Exception {
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
        throw new UnsupportedOperationException(); 
    }

    @Override
    public boolean receivedAlert() {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException(); 
    }
}
