package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.NamedGroup;

abstract class AbstractNegotiator implements Negotiator {

    final NamedGroup group;

    AbstractNegotiator(NamedGroup group) {
        this.group = group;
    }

}
