package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl.FFDHEImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl.SecpImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl.XImpl;

public interface NamedGroup extends Struct {

    int ENCODING_LENGTH = 2;
    
    FFDHE ffdhe2048 = new FFDHEImpl(0x0100);
    FFDHE ffdhe3072 = new FFDHEImpl(0x0101);
    FFDHE ffdhe4096 = new FFDHEImpl(0x0102);
    FFDHE ffdhe6144 = new FFDHEImpl(0x0103);
    FFDHE ffdhe8192 = new FFDHEImpl(0x0104);
    Secp secp256r1 = new SecpImpl(0x0017, "secp256r1");
    Secp secp384r1 = new SecpImpl(0x0018, "secp384r1");
    Secp secp521r1 = new SecpImpl(0x0019, "secp521r1");
    X x25519 = new XImpl(0x001D);
    X x448 = new XImpl(0x001E);

    public static interface Secp extends NamedGroup {

        String getCurve();
    }
    
    public static interface X extends NamedGroup {
        
    }
    
    public static interface FFDHE extends NamedGroup {
        
    }
    
}
