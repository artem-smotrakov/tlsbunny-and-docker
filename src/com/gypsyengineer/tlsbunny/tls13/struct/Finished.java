package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Bytes;

public interface Finished extends HandshakeMessage {

    byte[] getVerifyData();
}
