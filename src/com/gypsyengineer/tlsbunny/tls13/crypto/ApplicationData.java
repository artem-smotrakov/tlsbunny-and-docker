package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.tls13.struct.Alert;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import static com.gypsyengineer.tlsbunny.utils.Utils.EMPTY_ARRAY;

public class ApplicationData {

    private final AEAD encryptor;
    private final AEAD decryptor;
    private Alert receivedAlert;
    private boolean receivedNewSessionTicket = false;
    private boolean unexpectedResult = false;

    public ApplicationData(AEAD encryptor, AEAD decryptor) {
        this.encryptor = encryptor;
        this.decryptor = decryptor;
    }

    public boolean receivedAlert() {
        return receivedAlert != null;
    }

    public Alert getReceivedAlert() {
        return receivedAlert;
    }

    public boolean receivedNewSessionTicket() {
        return receivedNewSessionTicket;
    }
    
    public boolean unexpectedResult() {
        return unexpectedResult;
    }

    public byte[] decrypt(byte[] ciphertext) throws Exception {
        byte[] plaintext = decryptor.decrypt(ciphertext);
        TLSInnerPlaintext tlsInnerPlaintext = TLSInnerPlaintext.parse(plaintext);

        if (tlsInnerPlaintext.containsAlert()) {
            receivedAlert = Alert.parse(tlsInnerPlaintext.getContent());
            return EMPTY_ARRAY;
        }

        receivedNewSessionTicket = false;
        if (tlsInnerPlaintext.containsHandshake()) {
            Handshake handshake = Handshake.parse(tlsInnerPlaintext.getContent());
            if (handshake.containsNewSessionTicket()) {
                receivedNewSessionTicket = true;
            } else {
                unexpectedResult = true;
            }
            
            return EMPTY_ARRAY;
        }

        if (tlsInnerPlaintext.containsAlert() 
                || !tlsInnerPlaintext.containsApplicationData()) {
            
            unexpectedResult = true;
            return EMPTY_ARRAY;
        }

        return tlsInnerPlaintext.getContent();
    }

    public byte[] encrypt(byte[] plaintext) throws Exception {
        return encryptor.encrypt(
                TLSInnerPlaintext.noPadding(ContentType.application_data, plaintext)
                        .encoding());
    }

}
