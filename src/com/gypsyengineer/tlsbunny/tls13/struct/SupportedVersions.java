/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ProtocolVersionImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface SupportedVersions extends Struct {

    public static interface ClientHello extends SupportedVersions {

        int VERSIONS_LENGTH_BYTES = 1;

        byte[] encoding() throws IOException;

        int encodingLength();

        Vector<ProtocolVersion> getVersions();

        void setVersions(Vector<ProtocolVersion> versions);

    }
    
    public static interface ServerHello extends SupportedVersions {

        byte[] encoding() throws IOException;

        int encodingLength();

        ProtocolVersionImpl getSelectedVersion();

        void setSelectedVersion(ProtocolVersionImpl selected_version);

    }
    
}
