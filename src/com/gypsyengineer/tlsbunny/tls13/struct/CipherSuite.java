package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import java.nio.ByteBuffer;

public class CipherSuite implements Entity {

    public static final int ENCODING_LENGTH = 2;
    public static final String UNKNOWN = "unknown";
    
    public static final CipherSuite TLS_AES_128_GCM_SHA256          = new CipherSuite(0x13, 0x01);
    public static final CipherSuite TLS_AES_256_GCM_SHA384          = new CipherSuite(0x13, 0x02);
    public static final CipherSuite TLS_CHACHA20_POLY1305_SHA256    = new CipherSuite(0x13, 0x03);
    public static final CipherSuite TLS_AES_128_CCM_SHA256          = new CipherSuite(0x13, 0x04);
    public static final CipherSuite TLS_AES_128_CCM_8_SHA256        = new CipherSuite(0x13, 0x05);

    private int first;
    private int second;

    public CipherSuite(int first, int second) {
        check(first);
        check(second);
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        check(first);
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        check(second);
        this.second = second;
    }
    
    public AEAD.Cipher cipher() {
        if (first != 0x13) {
            return AEAD.Cipher.UNKNOWN;
        }
        
        switch (second) {
            case 0x01:
                return AEAD.Cipher.AES_128_GCM;
            case 0x02:
                return AEAD.Cipher.AES_256_GCM;
            case 0x03:
                return AEAD.Cipher.CHACHA20_POLY1305;
            case 0x04:
                return AEAD.Cipher.AES_128_CCM;
            case 0x05:
                return AEAD.Cipher.AES_128_CCM_8;
        }
        
        return AEAD.Cipher.UNKNOWN;
    }
    
    public int keyLength() {
        if (first != 0x13) {
            return 0;
        }
        
        switch (second) {
            case 0x01:
            case 0x04:
            case 0x05:
                return 16;
            case 0x02:
            case 0x03:
                return 32;
        }
        
        return 0;
    }
    
    public int ivLength() {
        if (first != 0x13) {
            return 0;
        }
        
        switch (second) {
            case 0x01:
            case 0x04:
            case 0x05:
                return 12;
            case 0x02:
            case 0x03:
                // TODO: fix it
                throw new RuntimeException("I don't know!");
        }
        
        return 0;
    }
    
    public String hash() {
        if (first != 0x13) {
            return UNKNOWN;
        }
        
        switch (second) {
            case 0x01:
            case 0x03:
            case 0x04:
            case 0x05:
                return "SHA-256";
            case 0x02:
                return "SHA-384";
        }
        
        return UNKNOWN;
    }
    
    public int hashLength() {
        if (first != 0x13) {
            return 0;
        }
        
        switch (second) {
            case 0x01:
            case 0x03:
            case 0x04:
            case 0x05:
                return 32;
            case 0x02:
                return 48;
        }
        
        return 0;
    }

    @Override
    public int encodingLength() {
        return ENCODING_LENGTH;
    }

    @Override
    public byte[] encoding() {
        return new byte[] { (byte) first, (byte) second };
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.first;
        hash = 79 * hash + this.second;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CipherSuite other = (CipherSuite) obj;
        if (this.first != other.first) {
            return false;
        }
        return this.second == other.second;
    }
    
    public static CipherSuite parse(ByteBuffer buffer) {
        return new CipherSuite(buffer.get() & 0xFF, buffer.get() & 0xFF);
    }

    private static void check(int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException();
        }
    }

}
