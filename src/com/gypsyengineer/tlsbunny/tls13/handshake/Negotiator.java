package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;

public interface Negotiator {

    KeyShareEntry createKeyShareEntry() throws Exception;
    void processKeyShareEntry(KeyShareEntry entry) throws Exception;
    byte[] generateSecret();

    public static Negotiator create(NamedGroup group) throws Exception {
        if (group instanceof NamedGroup.FFDHE) {
            return FFDHENegotiator.create((NamedGroup.FFDHE) group);
        }

        if (group instanceof NamedGroup.Secp) {
            return ECDHENegotiator.create((NamedGroup.Secp) group);
        }

        throw new IllegalArgumentException();
    }

}
