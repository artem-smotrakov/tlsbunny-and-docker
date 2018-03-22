package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.Utils;

import javax.crypto.Cipher;
import java.io.IOException;
import java.security.Key;

public abstract class AesGcm extends AbstractAEAD {

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

    @Override
    public byte[] decrypt(TLSPlaintext tlsCiphertext) throws Exception  {
        throw new UnsupportedOperationException();
    }

    byte[] getAdditionalData(ContentType type, ProtocolVersion version, UInt16 length) throws IOException {
        return Utils.concatenate(type.encoding(), version.encoding(), length.encoding());
    }

    byte[] getAdditionalData(TLSPlaintext tlsPlaintext) throws IOException {
        return getAdditionalData(
                tlsPlaintext.getType(),
                tlsPlaintext.getLegacyRecordVersion(),
                tlsPlaintext.getFragmentLength());
    }
}
