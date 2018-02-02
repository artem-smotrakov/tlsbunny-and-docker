package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;

// TODO: make it immutable
public interface KeyShare extends Struct {
    
    public static interface ClientHello extends KeyShare {

        int LENGTH_BYTES = 2;

        void clear();
        void set(KeyShareEntry keyShareEntry);
        void set(Vector<KeyShareEntry> client_shares);
    }

    public static interface ServerHello extends KeyShare {

        KeyShareEntry getServerShare();
    }
    
    public interface HelloRetryRequest extends KeyShare {

        NamedGroup getSelectedGroup();
    }
}
