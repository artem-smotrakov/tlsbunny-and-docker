package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;

abstract class AbstractNegotiator implements Negotiator {

    final NamedGroup group;
    final StructFactory factory;

    AbstractNegotiator(NamedGroup group, StructFactory factory) {
        this.group = group;
        this.factory = factory;
    }

}
