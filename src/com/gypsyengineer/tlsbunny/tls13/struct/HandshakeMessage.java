package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;

public interface HandshakeMessage extends Entity {
    HandshakeType type();
}
