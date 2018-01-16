package com.gypsyengineer.tlsbunny.tls;

import java.io.IOException;

public interface Entity {
    int encodingLength();
    byte[] encoding() throws IOException;
}
