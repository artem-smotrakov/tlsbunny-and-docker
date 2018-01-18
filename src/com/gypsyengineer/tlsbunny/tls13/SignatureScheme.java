package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SignatureScheme implements Entity {

    public static final int ENCODING_LENGTH = 2;
    
    public static final SignatureScheme rsa_pkcs1_sha256 = new SignatureScheme(0x0401);
    public static final SignatureScheme rsa_pkcs1_sha384 = new SignatureScheme(0x0501);
    public static final SignatureScheme rsa_pkcs1_sha512 = new SignatureScheme(0x0601);
    public static final SignatureScheme ecdsa_secp256r1_sha256 = new SignatureScheme(0x0403);
    public static final SignatureScheme ecdsa_secp384r1_sha384 = new SignatureScheme(0x0503);
    public static final SignatureScheme ecdsa_secp521r1_sha512 = new SignatureScheme(0x0603);
    public static final SignatureScheme rsa_pss_sha256 = new SignatureScheme(0x0804);
    public static final SignatureScheme rsa_pss_sha384 = new SignatureScheme(0x0805);
    public static final SignatureScheme rsa_pss_sha512 = new SignatureScheme(0x0806);
    public static final SignatureScheme ed25519 = new SignatureScheme(0x0807);
    public static final SignatureScheme ed448 = new SignatureScheme(0x0808);
    public static final SignatureScheme rsa_pkcs1_sha1 = new SignatureScheme(0x0201);
    public static final SignatureScheme ecdsa_sha1 = new SignatureScheme(0x0203);

    private int code;

    public SignatureScheme(int code) {
        check(code);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        check(code);
        this.code = code;
    }

    @Override
    public int encodingLength() {
        return ENCODING_LENGTH;
    }

    @Override
    public byte[] encoding() throws IOException {
        return ByteBuffer.allocate(ENCODING_LENGTH).putShort((short) code).array();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + this.code;
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
        final SignatureScheme other = (SignatureScheme) obj;
        return this.code == other.code;
    }

    public static SignatureScheme parse(ByteBuffer buffer) {
        return new SignatureScheme(buffer.getShort());
    }

    private static void check(int code) {
        if (code < 0 || code > 65535) {
            throw new IllegalArgumentException();
        }
    }
}
