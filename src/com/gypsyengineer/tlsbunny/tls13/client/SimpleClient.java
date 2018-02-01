package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.handshake.ClientHandshaker;
import com.gypsyengineer.tlsbunny.tls13.handshake.ECDHENegotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CipherSuiteImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.StructFactoryImpl;
import com.gypsyengineer.tlsbunny.utils.CertificateHolder;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Parameters;

public class SimpleClient {
    
    public static final byte[] HTTP_GET_REQUEST = "GET / HTTP/1.1\n\n".getBytes();

    public static void main(String[] args) throws Exception {
        ClientHandshaker handshaker = ClientHandshaker.create(StructFactoryImpl.getDefault(),
                SignatureSchemeImpl.ecdsa_secp256r1_sha256, 
                NamedGroupImpl.secp256r1, 
                ECDHENegotiator.create(NamedGroupImpl.secp256r1), 
                CipherSuiteImpl.TLS_AES_128_GCM_SHA256, 
                CertificateHolder.NO_CERTIFICATE);
        
        try (Connection connection = Connection.create(
                Parameters.getHost(), Parameters.getPort())) {
            
            ApplicationDataChannel applicationData = handshaker.start(connection);
            applicationData.send(HTTP_GET_REQUEST);
            byte[] bytes = applicationData.receive();
            System.out.printf("[tlsbunny] received data (%d bytes)%n", bytes.length);
            System.out.println(new String(bytes));
        }
    }
}
