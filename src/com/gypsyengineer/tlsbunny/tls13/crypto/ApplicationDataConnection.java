package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.tls13.struct.Alert;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.Connection;

public class ApplicationDataConnection {

    private final ApplicationData applicationData;
    private final Connection connection;

    public ApplicationDataConnection(
            ApplicationData applicationData, Connection connection) {

        this.applicationData = applicationData;
        this.connection = connection;
    }

    public boolean receivedAlert() {
        return applicationData.receivedAlert();
    }

    public Alert getReceivedAlert() {
        return applicationData.getReceivedAlert();
    }

    public boolean receivedNewSessionTicket() {
        return applicationData.receivedNewSessionTicket();
    }
    
    public boolean unexpectedResult() {
        return applicationData.unexpectedResult();
    }

    public byte[] read() throws Exception {
        TLSPlaintext tlsPlaintext = TLSPlaintext.parse(connection.read());

        if (!tlsPlaintext.containsApplicationData()) {
            throw new RuntimeException();
        }

        byte[] plaintext = applicationData.decrypt(tlsPlaintext.getFragment());
        
        if (applicationData.unexpectedResult()) {
            throw new RuntimeException();
        }
        
        return plaintext;
    }

    public void write(byte[] data) throws Exception {
        TLSPlaintext[] tlsPlaintexts = TLSPlaintext.wrap(
                ContentType.application_data, 
                ProtocolVersion.TLSv10, 
                applicationData.encrypt(
                        TLSInnerPlaintext.noPadding(
                                ContentType.application_data, data).encoding())
                );
        
        if (applicationData.unexpectedResult()) {
            throw new RuntimeException();
        }
        
        for (TLSPlaintext tlsPlaintext : tlsPlaintexts) {
            connection.send(tlsPlaintext.encoding());
        }
    }

}

