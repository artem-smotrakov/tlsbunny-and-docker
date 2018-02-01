package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls13.struct.EndOfEarlyData;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import java.io.IOException;

public class EndOfEarlyDataImpl implements EndOfEarlyData {

    EndOfEarlyDataImpl() {
    
    }

    @Override
    public int encodingLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] encoding() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public HandshakeType type() {
        return HandshakeType.end_of_early_data;
    }

}
