package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.ExtensionType;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;

public class ExtensionImpl implements Extension {

    private final ExtensionType extension_type;
    private final Vector<Byte> extension_data;
    
    ExtensionImpl(ExtensionType extension_type, Vector<Byte> extension_data) {
        this.extension_type = extension_type;
        this.extension_data = extension_data;
    }
    
    @Override
    public ExtensionType getExtensionType() {
        return extension_type;
    }
    
    @Override
    public Vector<Byte> getExtensionData() {
        return extension_data;
    }
    
    @Override
    public final int encodingLength() {
        return Utils.getEncodingLength(extension_type, extension_data);
    }

    @Override
    public final byte[] encoding() throws IOException {     
        return Utils.encoding(extension_type, extension_data);
    }

}
