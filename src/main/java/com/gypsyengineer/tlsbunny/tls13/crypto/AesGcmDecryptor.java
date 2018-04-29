package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesGcmDecryptor extends AesGcm {

    public AesGcmDecryptor(Cipher cipher, Key key, byte[] iv) {
        super(cipher, key, iv);
    }

    @Override
    public boolean supportDecryption() {
        return true;
    }

    @Override
    public void start() throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_IN_BITS, nextNonce()));
    }

    @Override
    public byte[] update(byte[] data) throws Exception {
        return cipher.update(data);
    }

    @Override
    public void updateAAD(byte[] data) throws Exception {
        cipher.updateAAD(data);
    }

    @Override
    public byte[] finish() throws Exception {
        return cipher.doFinal();
    }

    @Override
    public byte[] decrypt(TLSPlaintext tlsCiphertext) throws Exception {
        start();
        updateAAD(getAdditionalData(tlsCiphertext));
        update(tlsCiphertext.getFragment());
        return finish();
    }

    public static AesGcmDecryptor create(byte[] key, byte[] iv)
            throws NoSuchAlgorithmException, NoSuchPaddingException {

        return new AesGcmDecryptor(
                Cipher.getInstance(TRANSFORM),
                new SecretKeySpec(key, ALGORITHM), 
                iv);
    }

}
