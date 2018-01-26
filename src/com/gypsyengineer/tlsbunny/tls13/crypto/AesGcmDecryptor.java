package com.gypsyengineer.tlsbunny.tls13.crypto;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesGcmDecryptor extends AesGcm {

    public AesGcmDecryptor(javax.crypto.Cipher cipher, Key key, byte[] iv) {
        super(cipher, key, iv);
    }

    @Override
    public boolean supportDecryption() {
        return true;
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) throws Exception {
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, 
                new GCMParameterSpec(TAG_LENGTH, nextNonce()));
        return cipher.doFinal(ciphertext);
    }

    public static AesGcmDecryptor create(byte[] key, byte[] iv)
            throws NoSuchAlgorithmException, NoSuchPaddingException {

        return new AesGcmDecryptor(
                javax.crypto.Cipher.getInstance(TRANSFORM), 
                new SecretKeySpec(key, ALGORITHM), 
                iv);
    }

}
