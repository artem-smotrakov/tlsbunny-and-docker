package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Vector;

public interface HelloRetryRequest extends HandshakeMessage {

    int EXTENSIONS_LENGTH_BYTES = 2;

    void addExtension(Extension  extension);
    void clearExtensions();
    CipherSuite  getCipherSuite();
    Extension  getExtension(ExtensionType  type);
    Vector<Extension > getExtensions();
    ProtocolVersion  getProtocolVersion();
    void setCipherSuite(CipherSuite  cipher_suite);
    void setExtensions(Vector<Extension > extensions);
    void setProtocolVersion(ProtocolVersion  server_version);
}
