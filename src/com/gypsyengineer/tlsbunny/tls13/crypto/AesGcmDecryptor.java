package com.gypsyengineer.tlsbunny.tls13.crypto;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesGcmDecryptor extends AesGcm {

    public static final AEAD.Factory FACTORY = (key, iv) -> create(key, iv);

    public AesGcmDecryptor(Cipher cipher, Key key, byte[] iv) {
        super(cipher, key, iv);
    }

    @Override
    public boolean supportDecryption() {
        return true;
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key, 
                new GCMParameterSpec(TAG_LENGTH, nextNonce()));
        return cipher.doFinal(ciphertext);
    }

    public static AesGcmDecryptor create(byte[] key, byte[] iv)
            throws NoSuchAlgorithmException, NoSuchPaddingException {

        return new AesGcmDecryptor(
                Cipher.getInstance(TRANSFORM), 
                new SecretKeySpec(key, ALGORITHM), 
                iv);
    }

}
