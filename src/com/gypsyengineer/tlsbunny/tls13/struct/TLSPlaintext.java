package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ContentTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ProtocolVersionImpl;
import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import java.io.IOException;

public interface TLSPlaintext extends Struct {

    int MAX_ALLOWED_LENGTH = 16384;

    boolean containsAlert();

    boolean containsApplicationData();

    boolean containsHandshake();

    byte[] encoding() throws IOException;

    int encodingLength();

    byte[] getFragment();

    ProtocolVersionImpl getLegacyRecordVersion();

    UInt16 getLength();

    ContentTypeImpl getType();

    void setFragment(Bytes fragment);

    void setLegacyRecordVersion(ProtocolVersionImpl version);

    void setLength(UInt16 length);

    void setType(ContentTypeImpl type);
    
}
