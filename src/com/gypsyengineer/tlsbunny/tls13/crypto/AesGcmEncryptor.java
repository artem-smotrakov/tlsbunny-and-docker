package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.utils.Utils;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesGcmEncryptor extends AesGcm {

    private List<byte[]> ciphertexts = new ArrayList<>();

    public AesGcmEncryptor(Cipher cipher, Key key, byte[] iv) {
        super(cipher, key, iv);
    }

    @Override
    public boolean supportEncryption() {
        return true;
    }

    @Override
    public void start() throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key,
                new GCMParameterSpec(TAG_LENGTH_IN_BITS, nextNonce()));
    }

    @Override
    public byte[] update(byte[] data) {
        byte[] ciphertext = cipher.update(data);
        ciphertexts.add(ciphertext);

        return ciphertext.clone();
    }

    @Override
    public void updateAAD(byte[] data) {
        cipher.updateAAD(data);
    }

    @Override
    public byte[] finish() throws BadPaddingException, IllegalBlockSizeException {
        ciphertexts.add(cipher.doFinal());

        return Utils.concatenate(ciphertexts);
    }

    public static AesGcmEncryptor create(byte[] key, byte[] iv)
            throws NoSuchAlgorithmException, NoSuchPaddingException {

        return new AesGcmEncryptor(
                Cipher.getInstance(TRANSFORM),
                new SecretKeySpec(key, ALGORITHM), 
                iv);
    }

}
