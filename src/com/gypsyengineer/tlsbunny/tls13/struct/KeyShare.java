package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;

public interface KeyShare extends Struct {
    
    public static interface ClientHello extends KeyShare {

        int LENGTH_BYTES = 2;

        void clear();
        void set(KeyShareEntry keyShareEntry);
        void set(Vector<KeyShareEntry> client_shares);
    }

    public static interface ServerHello extends KeyShare {

        KeyShareEntry getServerShare();
        void setServerShare(KeyShareEntry server_share);
    }
    
    public interface HelloRetryRequest extends KeyShare {

        NamedGroup getSelectedGroup();
        void setSelectedGroup(NamedGroup selected_group);
    }
}
