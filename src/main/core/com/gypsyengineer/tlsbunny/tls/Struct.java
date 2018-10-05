package com.gypsyengineer.tlsbunny.tls;

import java.io.IOException;

public interface Struct {
    int encodingLength();
    byte[] encoding() throws IOException;
    Struct copy();
}
