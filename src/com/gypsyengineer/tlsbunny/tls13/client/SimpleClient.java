package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataConnection;
import com.gypsyengineer.tlsbunny.tls13.handshake.ClientHandshaker;
import com.gypsyengineer.tlsbunny.tls13.handshake.ECDHENegotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.utils.CertificateHolder;
import com.gypsyengineer.tlsbunny.utils.Connection;

public class SimpleClient {

    public static void main(String[] args) throws Exception {
        ClientHandshaker handshaker = ClientHandshaker.create(
                SignatureScheme.ecdsa_secp256r1_sha256, 
                NamedGroup.secp256r1, 
                ECDHENegotiator.create(NamedGroup.secp256r1), 
                CipherSuite.TLS_AES_128_GCM_SHA256, 
                CertificateHolder.NO_CERTIFICATE);
        
        try (Connection connection = Connection.create(
                System.getProperty("tlsbunny.host", "localhost"), 
                Integer.getInteger("tlsbunny.port", 443))) {
            
            handshaker.run(connection);
            ApplicationDataConnection appDataConnection = 
                    new ApplicationDataConnection(
                            handshaker.applicationData(), connection);
            
            appDataConnection.write("GET / HTTP/1.1\n\n".getBytes());
            
            byte[] bytes = appDataConnection.read();
            if (appDataConnection.receivedNewSessionTicket()) {
                System.out.println("[tlsbunny] received new session ticket");
                bytes = appDataConnection.read();
            }
            
            System.out.println("[tlsbunny] received data");
            System.out.println(new String(bytes));
        }
    }
}
