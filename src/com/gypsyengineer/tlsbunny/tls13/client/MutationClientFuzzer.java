package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory;
import com.gypsyengineer.tlsbunny.tls13.handshake.ClientHandshaker;
import com.gypsyengineer.tlsbunny.tls13.handshake.ECDHENegotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.CertificateHolder;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Parameters;
import static com.gypsyengineer.tlsbunny.utils.Utils.achtung;
import static com.gypsyengineer.tlsbunny.utils.Utils.info;

public class MutationClientFuzzer {
    
    public static final int DEFAULT_TESTS_NUMBER = 10000;
    public static final byte[] HTTP_GET_REQUEST = "GET / HTTP/1.1\n\n".getBytes();

    public static void main(String[] args) throws Exception {
        MutatedStructFactory fuzzer = new MutatedStructFactory(
                StructFactory.getDefault());
        
        String[] availableTargets = fuzzer.getAvailableTargets();
        info("fuzzer has the following targets:%n    %s", 
                String.join(",", availableTargets));
        
        ClientHandshaker handshaker = ClientHandshaker.create(
                fuzzer,
                SignatureScheme.ecdsa_secp256r1_sha256, 
                NamedGroup.secp256r1, 
                ECDHENegotiator.create(NamedGroup.secp256r1, fuzzer), 
                CipherSuite.TLS_AES_128_GCM_SHA256, 
                CertificateHolder.NO_CERTIFICATE);
        
        String host = Parameters.getHost();
        int port = Parameters.getPort();
        info("okay, we're going to fuzz %s:%d", host, port);
        
        String[] targets = Parameters.getTargets();
        String state = Parameters.getState();
        int testsNumber = Parameters.getTestsNumber();
                
        if (targets.length != 0 && !state.isEmpty()) {
            throw new IllegalArgumentException("Both targets and state are set!");
        }
        
        if (targets.length == 0 && state.isEmpty()) {
            throw new IllegalArgumentException("Set target or state");
        }
        
        if (targets.length != 0) {
            info("okay, we're going to fuzz the following targets:%n    %s", 
                    String.join(",", targets));
            info("we're going to run %d tests for each target", testsNumber);
        }
        
        if (!state.isEmpty()) {
            info("okay, let's set fuzzer's state to %s", state);
            info("then, we're going to run %d tests", testsNumber);
            fuzzer.setState(state);
        }
        
        for (String target : targets) {
            info("we're going to fuzz target '%s'", target);
            fuzzer.setState(target);
            
            int test = 0;
            while (fuzzer.canFuzz() && test < testsNumber) {
                info("test #%d: now fuzzer's state is '%s'", test, fuzzer.getState());
                try (Connection connection = Connection.create(host, port)) {
                    info("start handshaking");
                    ApplicationDataChannel applicationData = handshaker.start(connection);
                    
                    if (handshaker.receivedAlert()) {
                        info("received alert: %s", handshaker.getReceivedAlert());
                        continue;
                    }
                    
                    if (applicationData == null) {
                        // TODO: check what happened
                        info("handshake failed, couldn't create application data channel");
                        continue;
                    }
                    
                    applicationData.send(HTTP_GET_REQUEST);
                    byte[] bytes = applicationData.receive();
                    info("we just received %d bytes:%n%s", 
                            bytes.length, new String(bytes));
                    achtung("holy moly, connection succeeded!");
                } finally {
                    test++;
                    fuzzer.moveOn();
                }
            }
        }
    }
}
