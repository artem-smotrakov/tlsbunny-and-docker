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

    enum Method {
        AES_128_GCM,
        AES_256_GCM,
        CHACHA20_POLY1305,
        AES_128_CCM,
        AES_128_CCM_8,
        UNKNOWN
    }

    interface Factory {
        AEAD create(byte[] key, byte[] iv) throws Exception;
    }

    boolean supportEncryption();
    boolean supportDecryption();
    
    int getKeyLength();
    int getIvLength();

    void start() throws AEADException;
    byte[] update(byte[] data) throws AEADException;
    void updateAAD(byte[] data) throws AEADException;
    byte[] finish() throws AEADException;

    byte[] decrypt(TLSPlaintext tlsCiphertext) throws AEADException;
    
    static AEAD createEncryptor(Method cipher, byte[] key, byte[] iv)
            throws AEADException {
        
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
            throws AEADException {
        
        switch (cipher) {
            case AES_128_GCM:
            case AES_256_GCM:
                return AesGcmDecryptor.create(key, iv);
            case CHACHA20_POLY1305:
            case AES_128_CCM:
            case AES_128_CCM_8:
                throw new IllegalArgumentException("Unsupported cipher: " + cipher);
            default:
                throw new IllegalArgumentException("Unknown cipher: " + cipher);
        }
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
