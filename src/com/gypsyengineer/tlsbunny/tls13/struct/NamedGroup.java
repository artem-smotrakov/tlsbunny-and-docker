/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl.FFDHEImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl.SecpImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl.XImpl;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface NamedGroup extends Struct {

    int ENCODING_LENGTH = 2;
    FFDHEImpl ffdhe2048 = new FFDHEImpl(0x0100);
    FFDHEImpl ffdhe3072 = new FFDHEImpl(0x0101);
    FFDHEImpl ffdhe4096 = new FFDHEImpl(0x0102);
    FFDHEImpl ffdhe6144 = new FFDHEImpl(0x0103);
    FFDHEImpl ffdhe8192 = new FFDHEImpl(0x0104);
    SecpImpl secp256r1 = new SecpImpl(0x0017, "secp256r1");
    SecpImpl secp384r1 = new SecpImpl(0x0018, "secp384r1");
    SecpImpl secp521r1 = new SecpImpl(0x0019, "secp521r1");
    XImpl x25519 = new XImpl(0x001D);
    XImpl x448 = new XImpl(0x001E);

    byte[] encoding() throws IOException;

    int encodingLength();

    boolean equals(Object obj);

    int hashCode();
    
    public static interface Secp extends NamedGroup {

        String getCurve();
        
    }
    
    public static interface X extends NamedGroup {
        
    }
    
    public static interface FFDHE extends NamedGroup {
        
    }
    
}
