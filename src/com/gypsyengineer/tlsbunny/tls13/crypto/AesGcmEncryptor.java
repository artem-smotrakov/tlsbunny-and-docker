package com.gypsyengineer.tlsbunny.tls13.crypto;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesGcmEncryptor extends AesGcm {

    public AesGcmEncryptor(javax.crypto.Cipher cipher, Key key, byte[] iv) {
        super(cipher, key, iv);
    }

    @Override
    public boolean supportEncryption() {
        return true;
    }

    @Override
    public byte[] encrypt(byte[] plaintext) throws Exception {
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key, 
                new GCMParameterSpec(TAG_LENGTH, nextNonce()));
        return cipher.doFinal(plaintext);
    }

    public static AesGcmEncryptor create(byte[] key, byte[] iv)
            throws NoSuchAlgorithmException, NoSuchPaddingException {

        return new AesGcmEncryptor(
                javax.crypto.Cipher.getInstance(TRANSFORM), 
                new SecretKeySpec(key, ALGORITHM), 
                iv);
    }

}
