package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;

public interface ClientHello extends HandshakeMessage {

    int CIPHER_SUITES_LENGTH_BYTES = 2;
    int EXTENSIONS_LENGTH_BYTES = 2;
    int LEGACY_COMPRESSION_METHODS_LENGTH_BYTES = 1;
    int LEGACY_SESSION_ID_LENGTH_BYTES = 1;

    void addCipherSuite(CipherSuite cipher_suite);
    void addCompressionMethod(CompressionMethod method);
    void addExtension(Extension extension);
    void clearExtensions();
    Extension findExtension(ExtensionType type);
    Vector<CipherSuite> getCipherSuites();
    Vector<Extension> getExtensions();
    Vector<CompressionMethod> getLegacyCompressionMethods();
    Vector<Byte> getLegacySessionId();
    ProtocolVersion getProtocolVersion();
    Random getRandom();
    void setCipherSuites(Vector<CipherSuite> cipher_suites);
    void setCompressionMethods(Vector<CompressionMethod> legacy_compression_methods);
    void setExtensions(Vector<Extension> extensions);
    void setLegacySessionId(Vector<Byte> legacy_session_id);
    void setProtocolVersion(ProtocolVersion legacy_version);
    void setRandom(Random random);
}
