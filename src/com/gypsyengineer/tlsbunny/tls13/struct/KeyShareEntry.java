package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;

public interface KeyShareEntry extends Struct {

    int KEY_EXCHANGE_LENGTH_BYTES = 2;

    Vector<Byte> getKeyExchange();
    NamedGroup getNamedGroup();
    void setKeyExchange(Vector<Byte> key_exchange);
    void setNamedGroup(NamedGroup group);
}
