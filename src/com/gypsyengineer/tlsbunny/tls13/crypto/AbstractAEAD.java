package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.utils.Convertor;
import com.gypsyengineer.tlsbunny.utils.Utils;

public abstract class AbstractAEAD implements AEAD {

    private static final int DEFAULT_IV_LENGTH = 8;

    private long sequenceNumber = 0;
    private final byte[] iv;

    public AbstractAEAD(byte[] iv) {
        this.iv = iv;
    }
    
    public abstract int getNMin();
    
    @Override
    public final int getIvLength() {
        return Math.max(DEFAULT_IV_LENGTH, getNMin());
    }

    byte[] nextNonce() {
        return Utils.xor(Convertor.long2bytes(sequenceNumber++, iv.length), iv);
    }

    @Override
    public boolean supportEncryption() {
        return true;
    }

    @Override
    public boolean supportDecryption() {
        return false;
    }

    @Override
    public byte[] encrypt(byte[] plaintext) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getKeyLength() {
        throw new UnsupportedOperationException();
    }

}
