package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;

public interface Extension extends Struct {

    int EXTENSION_DATA_LENGTH_BYTES = 2;

    Vector<Byte> getExtensionData();
    ExtensionType getExtensionType();
    void setExtensionData(Vector<Byte> extension_data);
    void setExtensionType(ExtensionType extension_type);
}
