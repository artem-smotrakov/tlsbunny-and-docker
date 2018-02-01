package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.handshake.ClientHandshaker;
import com.gypsyengineer.tlsbunny.tls13.handshake.ECDHENegotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.CertificateHolder;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Parameters;

public class SimpleClient {
    
    public static final byte[] HTTP_GET_REQUEST = "GET / HTTP/1.1\n\n".getBytes();

    public static void main(String[] args) throws Exception {
        ClientHandshaker handshaker = ClientHandshaker.create(
                StructFactory.getDefault(),
                SignatureScheme.ecdsa_secp256r1_sha256, 
                NamedGroup.secp256r1, 
                ECDHENegotiator.create(NamedGroup.secp256r1), 
                CipherSuite.TLS_AES_128_GCM_SHA256, 
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
