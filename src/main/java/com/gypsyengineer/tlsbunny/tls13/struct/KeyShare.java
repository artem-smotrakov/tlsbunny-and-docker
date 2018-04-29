package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

public interface KeyShare extends Struct {
    
    public static interface ClientHello extends KeyShare {

        int LENGTH_BYTES = 2;
    }

    public static interface ServerHello extends KeyShare {

        KeyShareEntry getServerShare();
    }
    
    public interface HelloRetryRequest extends KeyShare {

        NamedGroup getSelectedGroup();
    }
}
