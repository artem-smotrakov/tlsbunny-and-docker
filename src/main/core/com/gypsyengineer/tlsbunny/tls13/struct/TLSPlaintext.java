package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.UInt16;

public interface TLSPlaintext extends Struct {

    int MAX_ALLOWED_LENGTH = 16384;

    // it's the length of an empty TLSPlaintext structure
    int MIN_ENCODING_LENGTH = ContentType.ENCODING_LENGTH
            + ProtocolVersion.ENCODING_LENGTH
            + UInt16.ENCODING_LENGTH;

    boolean containsAlert();
    boolean containsApplicationData();
    boolean containsHandshake();
    boolean containsChangeCipherSpec();
    byte[] getFragment();
    ProtocolVersion getLegacyRecordVersion();
    UInt16 getFragmentLength();
    ContentType getType();
}
