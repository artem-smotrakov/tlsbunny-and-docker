package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;

public class CipherSuiteImpl implements CipherSuite {

    private final int first;
    private final int second;

    CipherSuiteImpl(int first, int second) {
        check(first);
        check(second);
        this.first = first;
        this.second = second;
    }

    @Override
    public int getFirst() {
        return first;
    }

    @Override
    public int getSecond() {
        return second;
    }

    @Override
    public AEAD.Method cipher() {
        if (first != 0x13) {
            return AEAD.Method.UNKNOWN;
        }
        
        switch (second) {
            case 0x01:
                return AEAD.Method.AES_128_GCM;
            case 0x02:
                return AEAD.Method.AES_256_GCM;
            case 0x03:
                return AEAD.Method.CHACHA20_POLY1305;
            case 0x04:
                return AEAD.Method.AES_128_CCM;
            case 0x05:
                return AEAD.Method.AES_128_CCM_8;
        }
        
        return AEAD.Method.UNKNOWN;
    }
    
    @Override
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
    
    @Override
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
    
    @Override
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
    
    @Override
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
        final CipherSuiteImpl other = (CipherSuiteImpl) obj;
        if (this.first != other.first) {
            return false;
        }
        return this.second == other.second;
    }
    
    private static void check(int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException();
        }
    }

}
