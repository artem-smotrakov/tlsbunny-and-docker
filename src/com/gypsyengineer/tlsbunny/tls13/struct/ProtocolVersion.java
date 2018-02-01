package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ProtocolVersionImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;

public interface ProtocolVersion extends Struct {

    int ENCODING_LENGTH = 2;

    ProtocolVersion SSLv3  = new ProtocolVersionImpl(0x3, 0x0);
    ProtocolVersion TLSv10 = new ProtocolVersionImpl(0x3, 0x1);
    ProtocolVersion TLSv11 = new ProtocolVersionImpl(0x3, 0x2);
    ProtocolVersion TLSv12 = new ProtocolVersionImpl(0x3, 0x3);
    ProtocolVersion TLSv13 = new ProtocolVersionImpl(0x3, 0x4);
}
