package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeImpl;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TranscriptHash {

    private final MessageDigest md;

    private TranscriptHash(MessageDigest md) {
        this.md = md;
    }

    public void update(HandshakeImpl... messages) throws IOException {
        if (messages != null && messages.length != 0) {
            for (HandshakeImpl message : messages) {
                update(message.encoding());
            }
        } else {
            update(new byte[0]);
        }
    }
    
    public byte[] compute(HandshakeImpl... messages) throws IOException {
        reset();
        update(messages);
        return get();
    }

    public byte[] get() {
        return md.digest();
    }

    public void update(byte[] bytes) {
        md.update(bytes);
    }
    
    public void reset() {
        md.reset();
    }

    public static TranscriptHash create(String algorithm) 
            throws NoSuchAlgorithmException {
        
        return new TranscriptHash(MessageDigest.getInstance(algorithm));
    }

    public static byte[] compute(String algorithm, HandshakeImpl... messages)
            throws NoSuchAlgorithmException, IOException {

        TranscriptHash hash = create(algorithm);
       
        if (messages != null) {
            for (HandshakeImpl message : messages) {
                hash.update(message);
            } 
        } else {
            hash.update(new byte[0]);
        }
        
        return hash.get();
    }

}
