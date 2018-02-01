/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.CipherSuiteImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;

/**
 *
 * @author artem
 */
public interface CipherSuite extends Struct {

    int ENCODING_LENGTH = 2;
    CipherSuiteImpl TLS_AES_128_CCM_8_SHA256 = new CipherSuiteImpl(0x13, 0x05);
    CipherSuiteImpl TLS_AES_128_CCM_SHA256 = new CipherSuiteImpl(0x13, 0x04);
    CipherSuiteImpl TLS_AES_128_GCM_SHA256 = new CipherSuiteImpl(0x13, 0x01);
    CipherSuiteImpl TLS_AES_256_GCM_SHA384 = new CipherSuiteImpl(0x13, 0x02);
    CipherSuiteImpl TLS_CHACHA20_POLY1305_SHA256 = new CipherSuiteImpl(0x13, 0x03);
    String UNKNOWN = "unknown";

    AEAD.Cipher cipher();

    byte[] encoding();

    int encodingLength();

    boolean equals(Object obj);

    int getFirst();

    int getSecond();

    String hash();

    int hashCode();

    int hashLength();

    int ivLength();

    int keyLength();

    void setFirst(int first);

    void setSecond(int second);
    
}
