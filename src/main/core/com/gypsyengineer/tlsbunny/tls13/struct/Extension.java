package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;

public interface Extension extends Struct {

    int EXTENSION_DATA_LENGTH_BYTES = 2;

    Vector<Byte> getExtensionData();
    ExtensionType getExtensionType();
}
