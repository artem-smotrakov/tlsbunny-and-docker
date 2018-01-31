package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.struct.Certificate;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateVerify;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.EncryptedExtensions;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import com.gypsyengineer.tlsbunny.tls13.struct.HelloRetryRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.Connection;

public class ServerHandshaker extends AbstractHandshaker {

    public ServerHandshaker(SignatureScheme scheme, NamedGroup group, 
            Negotiator negotiator, CipherSuite ciphersuite, HKDF hkdf) {
        
        super(scheme, group, negotiator, ciphersuite, hkdf);
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
    public Certificate createCertificate() {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public CertificateVerify createCertificateVerify() throws Exception {
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
