package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls.Vector;

public interface HkdfLabel extends Struct {

    int CONTEXT_LENGTH_BYTES = 1;
    int LABEL_LENGTH_BYTES = 1;
    int MAX_CONTEXT_LENGTH = 255;
    int MAX_LABEL_LENGTH = 255;
    int MIN_CONTEXT_LENGTH = 0;
    int MIN_LABEL_LENGTH = 7;

    Vector<Byte> getContext();
    Vector<Byte> getLabel();
    UInt16 getLength();
}
