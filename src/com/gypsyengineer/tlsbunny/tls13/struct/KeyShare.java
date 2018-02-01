/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.KeyShareEntryImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface KeyShare {
    
    public static interface ClientHello {

        int LENGTH_BYTES = 2;

        void clear();

        byte[] encoding() throws IOException;

        int encodingLength();

        void set(KeyShareEntryImpl keyShareEntry);

        void set(Vector<KeyShareEntryImpl> client_shares);

    }

    
    public static interface ServerHello {

        byte[] encoding() throws IOException;

        int encodingLength();

        KeyShareEntryImpl getServerShare();

        void setServerShare(KeyShareEntryImpl server_share);

    }
    
    public interface HelloRetryRequest extends Struct {

        byte[] encoding() throws IOException;

        int encodingLength();

        NamedGroupImpl getSelectedGroup();

        void setSelectedGroup(NamedGroupImpl selected_group);

    }
}
