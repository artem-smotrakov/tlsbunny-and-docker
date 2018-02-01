package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.KeyShareEntryImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl;

public interface Negotiator {

    KeyShareEntryImpl createKeyShareEntry() throws Exception;
    void processKeyShareEntry(KeyShareEntryImpl entry) throws Exception;
    byte[] generateSecret();

    public static Negotiator create(NamedGroupImpl group) throws Exception {
        if (group instanceof NamedGroupImpl.FFDHEImpl) {
            return FFDHENegotiator.create((NamedGroupImpl.FFDHEImpl) group);
        }

        if (group instanceof NamedGroupImpl.SecpImpl) {
            return ECDHENegotiator.create((NamedGroupImpl.SecpImpl) group);
        }

        throw new IllegalArgumentException();
    }

}
