package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;

public interface SignatureScheme extends Struct {

    int ENCODING_LENGTH = 2;
    
    SignatureScheme ecdsa_secp256r1_sha256 = new SignatureSchemeImpl(0x0403);
    SignatureScheme ecdsa_secp384r1_sha384 = new SignatureSchemeImpl(0x0503);
    SignatureScheme ecdsa_secp521r1_sha512 = new SignatureSchemeImpl(0x0603);
    SignatureScheme ecdsa_sha1 = new SignatureSchemeImpl(0x0203);
    SignatureScheme ed25519 = new SignatureSchemeImpl(0x0807);
    SignatureScheme ed448 = new SignatureSchemeImpl(0x0808);
    SignatureScheme rsa_pkcs1_sha1 = new SignatureSchemeImpl(0x0201);
    SignatureScheme rsa_pkcs1_sha256 = new SignatureSchemeImpl(0x0401);
    SignatureScheme rsa_pkcs1_sha384 = new SignatureSchemeImpl(0x0501);
    SignatureScheme rsa_pkcs1_sha512 = new SignatureSchemeImpl(0x0601);
    SignatureScheme rsa_pss_sha256 = new SignatureSchemeImpl(0x0804);
    SignatureScheme rsa_pss_sha384 = new SignatureSchemeImpl(0x0805);
    SignatureScheme rsa_pss_sha512 = new SignatureSchemeImpl(0x0806);

    int getCode();
    void setCode(int code);
}
