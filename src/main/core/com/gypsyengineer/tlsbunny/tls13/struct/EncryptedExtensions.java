package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Vector;

public interface EncryptedExtensions extends HandshakeMessage {

    int LENGTH_BYTES = 2;

    Vector<Extension> getExtensions();
}
