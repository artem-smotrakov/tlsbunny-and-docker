package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.UInt16;

public interface TLSPlaintext extends Struct {

    int MAX_ALLOWED_LENGTH = 16384;

    boolean containsAlert();
    boolean containsApplicationData();
    boolean containsHandshake();
    boolean containsChangeCipherSpec();
    byte[] getFragment();
    ProtocolVersion getLegacyRecordVersion();
    UInt16 getFragmentLength();
    ContentType getType();
}
