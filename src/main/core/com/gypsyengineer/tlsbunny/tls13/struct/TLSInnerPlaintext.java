package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.utils.Utils;

public interface TLSInnerPlaintext extends Struct {

    byte[] NO_PADDING = Utils.EMPTY_ARRAY;

    boolean containsAlert();
    boolean containsApplicationData();
    boolean containsHandshake();
    byte[] getContent();
    ContentType getType();
    Bytes getZeros();
}
