/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface SignatureScheme extends Struct {

    int ENCODING_LENGTH = 2;
    SignatureSchemeImpl ecdsa_secp256r1_sha256 = new SignatureSchemeImpl(0x0403);
    SignatureSchemeImpl ecdsa_secp384r1_sha384 = new SignatureSchemeImpl(0x0503);
    SignatureSchemeImpl ecdsa_secp521r1_sha512 = new SignatureSchemeImpl(0x0603);
    SignatureSchemeImpl ecdsa_sha1 = new SignatureSchemeImpl(0x0203);
    SignatureSchemeImpl ed25519 = new SignatureSchemeImpl(0x0807);
    SignatureSchemeImpl ed448 = new SignatureSchemeImpl(0x0808);
    SignatureSchemeImpl rsa_pkcs1_sha1 = new SignatureSchemeImpl(0x0201);
    SignatureSchemeImpl rsa_pkcs1_sha256 = new SignatureSchemeImpl(0x0401);
    SignatureSchemeImpl rsa_pkcs1_sha384 = new SignatureSchemeImpl(0x0501);
    SignatureSchemeImpl rsa_pkcs1_sha512 = new SignatureSchemeImpl(0x0601);
    SignatureSchemeImpl rsa_pss_sha256 = new SignatureSchemeImpl(0x0804);
    SignatureSchemeImpl rsa_pss_sha384 = new SignatureSchemeImpl(0x0805);
    SignatureSchemeImpl rsa_pss_sha512 = new SignatureSchemeImpl(0x0806);

    byte[] encoding() throws IOException;

    int encodingLength();

    boolean equals(Object obj);

    int getCode();

    int hashCode();

    void setCode(int code);
    
}
