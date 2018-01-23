package com.gypsyengineer.tlsbunny.tls13.crypto;

public interface AEAD {

    public static enum Mode {
        None, GCM, CCM, CCM_8
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
}
