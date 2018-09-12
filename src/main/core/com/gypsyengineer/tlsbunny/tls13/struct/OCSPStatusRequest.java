package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;

public interface OCSPStatusRequest extends Struct {

    Vector<ResponderID> getResponderIdList();
    Vector<Byte> getExtensions();
}
