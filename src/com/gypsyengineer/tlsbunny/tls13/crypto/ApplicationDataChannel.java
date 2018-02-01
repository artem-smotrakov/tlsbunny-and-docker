package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ContentTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ProtocolVersionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.StructFactoryImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.TLSInnerPlaintextImpl;
import static com.gypsyengineer.tlsbunny.tls13.struct.impl.TLSInnerPlaintextImpl.NO_PADDING;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.TLSPlaintextImpl;
import com.gypsyengineer.tlsbunny.utils.Connection;

public class ApplicationDataChannel {
    
    private final AEAD encryptor;
    private final AEAD decryptor;
    private final Connection connection;
    private final StructFactoryImpl factory;
    
    public ApplicationDataChannel(StructFactoryImpl factory, Connection connection, 
            AEAD enctyptor, AEAD decryptor) {
        
        this.connection = connection;
        this.encryptor = enctyptor;
        this.decryptor = decryptor;
        this.factory = factory;
    }
    
    public byte[] receive() throws Exception {
        TLSPlaintext tlsPlaintext = TLSPlaintextImpl.parse(connection.read());

        if (!tlsPlaintext.containsApplicationData()) {
            throw new RuntimeException();
        }

        return decrypt(tlsPlaintext.getFragment());
    }

    public void send(byte[] data) throws Exception {
        TLSPlaintextImpl[] tlsPlaintexts = factory.createTLSPlaintexts(ContentTypeImpl.application_data, 
                ProtocolVersionImpl.TLSv10, 
                encrypt(factory.createTLSInnerPlaintext(ContentTypeImpl.application_data, data, NO_PADDING).encoding()));
        
        for (TLSPlaintext tlsPlaintext : tlsPlaintexts) {
            connection.send(tlsPlaintext.encoding());
        }
    }
        
    public byte[] decrypt(byte[] ciphertext) throws Exception {
        return TLSInnerPlaintextImpl.parse(decryptor.decrypt(ciphertext)).getContent();
    }

    public byte[] encrypt(byte[] plaintext) throws Exception {
        return encryptor.encrypt(factory.createTLSInnerPlaintext(ContentTypeImpl.application_data, plaintext, NO_PADDING).encoding());
    }
}
