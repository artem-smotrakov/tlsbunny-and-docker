/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ProtocolVersionImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface ProtocolVersion extends Struct {

    int ENCODING_LENGTH = 2;
    ProtocolVersionImpl SSLv3 = new ProtocolVersionImpl(3, 0);
    ProtocolVersionImpl TLSv10 = new ProtocolVersionImpl(3, 1);
    ProtocolVersionImpl TLSv11 = new ProtocolVersionImpl(3, 2);
    ProtocolVersionImpl TLSv12 = new ProtocolVersionImpl(3, 3);
    ProtocolVersionImpl TLSv13 = new ProtocolVersionImpl(3, 4);

    byte[] encoding() throws IOException;

    int encodingLength();

    boolean equals(Object obj);

    int hashCode();
    
}
