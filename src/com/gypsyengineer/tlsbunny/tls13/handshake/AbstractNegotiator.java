package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl;

abstract class AbstractNegotiator implements Negotiator {

    final NamedGroupImpl group;

    AbstractNegotiator(NamedGroupImpl group) {
        this.group = group;
    }

}
