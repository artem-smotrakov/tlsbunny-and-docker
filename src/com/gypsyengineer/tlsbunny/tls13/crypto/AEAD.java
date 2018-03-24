package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.Utils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;

public interface AEAD {

    public static enum Method {
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

    void start() throws Exception;
    byte[] update(byte[] data) throws Exception;
    void updateAAD(byte[] data) throws Exception;
    byte[] finish() throws Exception;

    byte[] decrypt(TLSPlaintext tlsCiphertext) throws Exception;
    
    static AEAD createEncryptor(Method cipher, byte[] key, byte[] iv)
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
    
    static AEAD createDecryptor(Method cipher, byte[] key, byte[] iv)
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

    static byte[] getAdditionalData(int length) throws IOException {
        return getAdditionalData(new UInt16(length));
    }

    static byte[] getAdditionalData(TLSPlaintext tlsPlaintext) throws IOException {
        return getAdditionalData(tlsPlaintext.getFragmentLength());
    }

    static byte[] getAdditionalData(UInt16 length) throws IOException {
        return Utils.concatenate(
                ContentType.application_data.encoding(),
                ProtocolVersion.TLSv12.encoding(),
                length.encoding());
    }
}
