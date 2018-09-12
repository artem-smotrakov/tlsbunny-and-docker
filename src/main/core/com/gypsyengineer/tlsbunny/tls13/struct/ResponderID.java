package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;

public interface ResponderID extends Struct {

    static int LENGTH_BYTES = 2;

    Vector<Byte> getContent();
}
