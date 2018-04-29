package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;

public interface ServerHello extends HandshakeMessage {

    int EXTENSIONS_LENGTH_BYTES = 2;
    int LEGACY_SESSION_ID_ECHO_LENGTH_BYTES = 1;

    ProtocolVersion getProtocolVersion();
    Random getRandom();
    Vector<Byte> getLegacySessionIdEcho();
    CipherSuite getCipherSuite();
    CompressionMethod getLegacyCompressionMethod();
    Vector<Extension> getExtensions();

    Extension findExtension(ExtensionType type);
}
