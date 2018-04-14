package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

public interface ProtocolVersion extends Struct {

    int ENCODING_LENGTH = 2;

    ProtocolVersion SSLv3  = StructFactory.getDefault().createProtocolVestion(0x3, 0x0);
    ProtocolVersion TLSv10 = StructFactory.getDefault().createProtocolVestion(0x3, 0x1);
    ProtocolVersion TLSv11 = StructFactory.getDefault().createProtocolVestion(0x3, 0x2);
    ProtocolVersion TLSv12 = StructFactory.getDefault().createProtocolVestion(0x3, 0x3);
    ProtocolVersion TLSv13 = StructFactory.getDefault().createProtocolVestion(0x3, 0x4);

    int getMinor();
    int getMajor();
}
