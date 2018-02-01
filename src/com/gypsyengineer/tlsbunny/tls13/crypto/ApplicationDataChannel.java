package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import static com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext.NO_PADDING;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.Connection;

public class ApplicationDataChannel {
    
    private final AEAD encryptor;
    private final AEAD decryptor;
    private final Connection connection;
    private final StructFactory factory;
    
    public ApplicationDataChannel(StructFactory factory, Connection connection, 
            AEAD enctyptor, AEAD decryptor) {
        
        this.connection = connection;
        this.encryptor = enctyptor;
        this.decryptor = decryptor;
        this.factory = factory;
    }
    
    public byte[] receive() throws Exception {
        TLSPlaintext tlsPlaintext = factory.parseTLSPlaintext(connection.read());

        if (!tlsPlaintext.containsApplicationData()) {
            throw new RuntimeException();
        }

        return decrypt(tlsPlaintext.getFragment());
    }

    public void send(byte[] data) throws Exception {
        TLSPlaintext[] tlsPlaintexts = factory.createTLSPlaintexts(ContentType.application_data, 
                ProtocolVersion.TLSv10, 
                encrypt(factory.createTLSInnerPlaintext(ContentType.application_data, data, NO_PADDING).encoding()));
        
        for (TLSPlaintext tlsPlaintext : tlsPlaintexts) {
            connection.send(tlsPlaintext.encoding());
        }
    }
        
    public byte[] decrypt(byte[] ciphertext) throws Exception {
        return factory.parseTLSInnerPlaintext(decryptor.decrypt(ciphertext)).getContent();
    }

    public byte[] encrypt(byte[] plaintext) throws Exception {
        return encryptor.encrypt(factory.createTLSInnerPlaintext(ContentType.application_data, plaintext, NO_PADDING).encoding());
    }
}
