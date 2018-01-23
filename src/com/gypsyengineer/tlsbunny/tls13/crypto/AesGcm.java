    package com.gypsyengineer.tlsbunny.tls13.crypto;

import java.security.Key;
import javax.crypto.Cipher;

public class AesGcm extends AbstractAEAD {

    static final String ALGORITHM = "AES";
    static final String TRANSFORM = "AES/GCM/NoPadding";
    static final int TAG_LENGTH = 128;
    static final int N_MIN = 12;

    final Cipher cipher;
    final Key key;

    public AesGcm(Cipher cipher, Key key, byte[] iv) {
        super(iv);
        this.cipher = cipher;
        this.key = key;
    }

    @Override
    public int getNMin() {
        return N_MIN;
    }

}
