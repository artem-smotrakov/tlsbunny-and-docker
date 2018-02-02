package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Vector;

// TODO: make it immutable
public interface HelloRetryRequest extends HandshakeMessage {

    int EXTENSIONS_LENGTH_BYTES = 2;

    void addExtension(Extension  extension);
    void clearExtensions();
    CipherSuite  getCipherSuite();
    Extension  getExtension(ExtensionType  type);
    Vector<Extension > getExtensions();
    ProtocolVersion  getProtocolVersion();
}
