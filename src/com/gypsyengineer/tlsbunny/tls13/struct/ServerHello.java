/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.CipherSuiteImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ProtocolVersionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.KeyShareImpl;
import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface ServerHello extends HandshakeMessage {

    int EXTENSIONS_LENGTH_BYTES = 2;

    void addExtension(ExtensionImpl extension);

    void clearExtensions();

    byte[] encoding() throws IOException;

    int encodingLength();

    ExtensionImpl findExtension(ExtensionTypeImpl type);

    KeyShareImpl.ServerHelloImpl findKeyShare() throws IOException;

    CipherSuiteImpl getCipherSuite();

    Vector<ExtensionImpl> getExtensions();

    ProtocolVersionImpl getProtocolVersion();

    Random getRandom();

    void setCipherSuite(CipherSuiteImpl cipher_suite);

    void setExtensions(Vector<ExtensionImpl> extensions);

    void setProtocolVersion(ProtocolVersionImpl version);

    void setRandom(Random random);

    HandshakeTypeImpl type();
    
}
