package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;

// TODO: make it immutable
public interface ServerHello extends HandshakeMessage {

    int EXTENSIONS_LENGTH_BYTES = 2;

    void addExtension(Extension extension);
    void clearExtensions();
    Extension findExtension(ExtensionType type);
    CipherSuite getCipherSuite();
    Vector<Extension> getExtensions();
    ProtocolVersion getProtocolVersion();
    Random getRandom();
}
