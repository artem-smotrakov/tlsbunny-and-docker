package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;

public interface HandshakeMessage extends Struct {
    HandshakeTypeImpl type();
}
