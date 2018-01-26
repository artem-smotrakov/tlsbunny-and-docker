package com.gypsyengineer.tlsbunny.tls13.crypto;

import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;

public interface AEAD {

    public static enum Cipher {
        AES_128_GCM, AES_256_GCM, CHACHA20_POLY1305, AES_128_CCM, AES_128_CCM_8,
        UNKNOWN
    }

    public static interface Factory {
        AEAD create(byte[] key, byte[] iv) throws Exception;
    }

    boolean supportEncryption();
    boolean supportDecryption();
    
    int getKeyLength();
    int getIvLength();
    
    byte[] encrypt(byte[] plaintext) throws Exception ;
    byte[] decrypt(byte[] ciphertext) throws Exception;
    
    public static AEAD createEncryptor(Cipher cipher, byte[] key, byte[] iv) 
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        
        switch (cipher) {
            case AES_128_GCM:
            case AES_256_GCM:
                return AesGcmEncryptor.create(key, iv);
            case CHACHA20_POLY1305:
            case AES_128_CCM:
            case AES_128_CCM_8:
                throw new IllegalArgumentException();
        }
        
        throw new IllegalArgumentException();
    }
    
    public static AEAD createDecryptor(Cipher cipher, byte[] key, byte[] iv) 
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        
        switch (cipher) {
            case AES_128_GCM:
            case AES_256_GCM:
                return AesGcmDecryptor.create(key, iv);
            case CHACHA20_POLY1305:
            case AES_128_CCM:
            case AES_128_CCM_8:
                throw new IllegalArgumentException();
        }
        
        throw new IllegalArgumentException();
    }
}
