package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;

abstract class AbstractNegotiator implements Negotiator {

    final NamedGroup group;

    AbstractNegotiator(NamedGroup group) {
        this.group = group;
    }

}
