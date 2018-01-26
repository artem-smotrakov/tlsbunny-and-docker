package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Entity;

public interface HandshakeMessage extends Entity {
    HandshakeType type();
}
