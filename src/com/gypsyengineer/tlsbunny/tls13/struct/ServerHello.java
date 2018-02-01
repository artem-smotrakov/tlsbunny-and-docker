package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;

public interface ServerHello extends HandshakeMessage {

    int EXTENSIONS_LENGTH_BYTES = 2;

    void addExtension(Extension extension);
    void clearExtensions();
    Extension findExtension(ExtensionType type);
    KeyShare.ServerHello findKeyShare() throws IOException;
    CipherSuite getCipherSuite();
    Vector<Extension> getExtensions();
    ProtocolVersion getProtocolVersion();
    Random getRandom();
    void setCipherSuite(CipherSuite cipher_suite);
    void setExtensions(Vector<Extension> extensions);
    void setProtocolVersion(ProtocolVersion version);
    void setRandom(Random random);
}
