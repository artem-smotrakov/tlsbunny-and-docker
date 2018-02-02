package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;

public interface ServerHello extends HandshakeMessage {

    int EXTENSIONS_LENGTH_BYTES = 2;

    Extension findExtension(ExtensionType type);
    CipherSuite getCipherSuite();
    Vector<Extension> getExtensions();
    ProtocolVersion getProtocolVersion();
    Random getRandom();
}
