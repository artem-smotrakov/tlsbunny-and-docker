package com.gypsyengineer.tlsbunny.tls13.struct;

import java.io.IOException;

public class EndOfEarlyData implements HandshakeMessage {

    EndOfEarlyData() {
    
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
