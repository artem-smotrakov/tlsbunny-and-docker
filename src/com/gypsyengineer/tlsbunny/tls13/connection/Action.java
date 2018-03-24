package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Connection;

import java.nio.ByteBuffer;

public interface Action {
    Action set(StructFactory factory);
    Action set(SignatureScheme scheme);
    Action set(NamedGroup group);
    Action set(CipherSuite suite);
    Action set(Negotiator negotiator);
    Action set(HKDF hkdf);
    Action set(Context context);
    Action set(ByteBuffer buffer);
    Action set(Connection connection);
    Action run();
    boolean succeeded();
    ByteBuffer data();
}
