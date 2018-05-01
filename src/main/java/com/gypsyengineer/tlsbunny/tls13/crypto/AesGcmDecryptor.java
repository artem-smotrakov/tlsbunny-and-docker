package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
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
    public void start() throws AEADException {
        try {
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    key,
                    new GCMParameterSpec(TAG_LENGTH_IN_BITS, nextNonce()));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new AEADException(e);
        }
    }

    @Override
    public byte[] update(byte[] data) {
        return cipher.update(data);
    }

    @Override
    public void updateAAD(byte[] data) {
        cipher.updateAAD(data);
    }

    @Override
    public byte[] finish() throws AEADException {
        try {
            return cipher.doFinal();
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new AEADException(e);
        }
    }

    @Override
    public byte[] decrypt(TLSPlaintext tlsCiphertext) throws AEADException {
        try {
            start();
            updateAAD(getAdditionalData(tlsCiphertext));
            update(tlsCiphertext.getFragment());
            return finish();
        } catch (IOException e) {
            throw new AEADException(e);
        }
    }

    public static AesGcmDecryptor create(byte[] key, byte[] iv) throws AEADException {
        try {
            return new AesGcmDecryptor(
                    Cipher.getInstance(TRANSFORM),
                    new SecretKeySpec(key, ALGORITHM),
                    iv);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new AEADException(e);
        }
    }

}
