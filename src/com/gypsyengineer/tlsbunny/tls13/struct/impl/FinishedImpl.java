package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;

public class FinishedImpl implements Finished {

    private final Bytes verify_data;

    FinishedImpl(Bytes verify_data) {
        this.verify_data = verify_data;
    }

    @Override
    public byte[] getVerifyData() {
        return verify_data.encoding();
    }

    @Override
    public int encodingLength() {
        return verify_data.encodingLength();
    }

    @Override
    public byte[] encoding() {
        return verify_data.encoding();
    }

    @Override
    public HandshakeType type() {
        return HandshakeType.finished;
    }

}
