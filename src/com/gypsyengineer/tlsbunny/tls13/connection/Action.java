package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Connection;

public interface Action {
    void init(StructFactory factory);
    void init(SignatureScheme scheme);
    void init(NamedGroup group);
    void init(Negotiator negotiator);
    void init(Context context);
    void init(byte[] data);
    void init(Connection connection);
    void run();
    boolean succeeded();
    byte[] data();
}
