package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import java.io.IOException;

public interface TLSPlaintext extends Struct {

    int MAX_ALLOWED_LENGTH = 16384;

    boolean containsAlert();
    boolean containsApplicationData();
    boolean containsHandshake();
    byte[] getFragment();
    ProtocolVersion getLegacyRecordVersion();
    UInt16 getLength();
    ContentType getType();
    void setFragment(Bytes fragment);
    void setLegacyRecordVersion(ProtocolVersion version);
    void setLength(UInt16 length);
    void setType(ContentType type);
}
