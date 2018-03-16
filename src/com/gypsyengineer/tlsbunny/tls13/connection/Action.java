package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Connection;

public interface Action {
    Action set(StructFactory factory);
    Action set(SignatureScheme scheme);
    Action set(NamedGroup group);
    Action set(Negotiator negotiator);
    Action set(Context context);
    Action set(byte[] data);
    Action set(Connection connection);
    Action run();
    boolean succeeded();
    byte[] data();
}
