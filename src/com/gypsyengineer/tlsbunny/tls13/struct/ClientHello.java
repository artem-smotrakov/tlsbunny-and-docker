/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.CompressionMethodImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.CipherSuiteImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupListImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ProtocolVersionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.KeyShareImpl;
import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeListImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SupportedVersionsImpl;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface ClientHello extends HandshakeMessage {

    int CIPHER_SUITES_LENGTH_BYTES = 2;
    int EXTENSIONS_LENGTH_BYTES = 2;
    int LEGACY_COMPRESSION_METHODS_LENGTH_BYTES = 1;
    int LEGACY_SESSION_ID_LENGTH_BYTES = 1;

    void addCipherSuite(CipherSuiteImpl cipher_suite);

    void addCompressionMethod(CompressionMethodImpl method);

    void addExtension(ExtensionImpl extension);

    void clearExtensions();

    byte[] encoding() throws IOException;

    int encodingLength();

    ExtensionImpl findExtension(ExtensionTypeImpl type);

    KeyShareImpl.ClientHelloImpl findKeyShare() throws IOException;

    SignatureSchemeListImpl findSignatureAlgorithms() throws IOException;

    NamedGroupListImpl findSupportedGroups() throws IOException;

    SupportedVersionsImpl.ClientHelloImpl findSupportedVersions() throws IOException;

    Vector<CipherSuiteImpl> getCipherSuites();

    Vector<ExtensionImpl> getExtensions();

    Vector<CompressionMethodImpl> getLegacyCompressionMethods();

    Vector<Byte> getLegacySessionId();

    ProtocolVersionImpl getProtocolVersion();

    Random getRandom();

    void setCipherSuites(Vector<CipherSuiteImpl> cipher_suites);

    void setCompressionMethods(Vector<CompressionMethodImpl> legacy_compression_methods);

    void setExtensions(Vector<ExtensionImpl> extensions);

    void setLegacySessionId(Vector<Byte> legacy_session_id);

    void setProtocolVersion(ProtocolVersionImpl legacy_version);

    void setRandom(Random random);

    HandshakeTypeImpl type();
    
}
