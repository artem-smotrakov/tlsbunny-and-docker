package com.gypsyengineer.tlsbunny.tls13;

import java.io.IOException;

public class EndOfEarlyData implements HandshakeMessage {

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
        return HandshakeType.END_OF_EARLY_DATA;
    }

}
