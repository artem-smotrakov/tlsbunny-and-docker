package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.utils.Utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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
    public void start() throws AEADException {
        ciphertexts.clear();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key,
                    new GCMParameterSpec(TAG_LENGTH_IN_BITS, nextNonce()));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new AEADException(e);
        }
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
    public byte[] finish() throws AEADException {
        try {
            ciphertexts.add(cipher.doFinal());
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new AEADException(e);
        }

        return Utils.concatenate(ciphertexts);
    }

    public static AesGcmEncryptor create(byte[] key, byte[] iv)
            throws AEADException {

        try {
            return new AesGcmEncryptor(
                    Cipher.getInstance(TRANSFORM),
                    new SecretKeySpec(key, ALGORITHM),
                    iv);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new AEADException(e);
        }
    }

}
