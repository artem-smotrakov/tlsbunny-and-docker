package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;

/**
 * uint16 ProtocolVersion;
 * opaque Random[32];
 *
 * uint8 CipherSuite[2];
 *
 * struct {
 *     ProtocolVersion legacy_version = 0x0303;    // TLS v1.2
 *     Random random;
 *     opaque legacy_session_id<0..32>;
 *     CipherSuite cipher_suites<2..2^16-2>;
 *     opaque legacy_compression_methods<1..2^8-1>;
 *     Extension extensions<8..2^16-1>;
 * } ClientHello;
 *
 */
public interface ClientHello extends HandshakeMessage {

    int CIPHER_SUITES_LENGTH_BYTES = 2;
    int EXTENSIONS_LENGTH_BYTES = 2;
    int LEGACY_COMPRESSION_METHODS_LENGTH_BYTES = 1;
    int LEGACY_SESSION_ID_LENGTH_BYTES = 1;

    Extension findExtension(ExtensionType type);
    Vector<CipherSuite> getCipherSuites();
    Vector<Extension> getExtensions();
    Vector<CompressionMethod> getLegacyCompressionMethods();
    Vector<Byte> getLegacySessionId();
    ProtocolVersion getProtocolVersion();
    Random getRandom();
}

