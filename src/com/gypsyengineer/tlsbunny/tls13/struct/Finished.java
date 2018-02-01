package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Bytes;

public interface Finished extends HandshakeMessage {

    byte[] getVerifyData();
    void setVerifyData(byte[] verify_data);
    void setVerifyData(Bytes verify_data);
}
