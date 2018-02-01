package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.ExtensionType;

public class ExtensionImpl implements Extension {

    private ExtensionType extension_type;
    private Vector<Byte> extension_data;
    
    public ExtensionImpl(ExtensionType extension_type, Vector<Byte> extension_data) {
        this.extension_type = extension_type;
        this.extension_data = extension_data;
    }
    
    @Override
    public ExtensionType getExtensionType() {
        return extension_type;
    }
    
    @Override
    public void setExtensionType(ExtensionType extension_type) {
        this.extension_type = extension_type;
    }
    
    @Override
    public Vector<Byte> getExtensionData() {
        return extension_data;
    }
    
    @Override
    public void setExtensionData(Vector<Byte> extension_data) {
        this.extension_data = extension_data;
    }

    @Override
    public final int encodingLength() {
        return Utils.getEncodingLength(extension_type, extension_data);
    }

    @Override
    public final byte[] encoding() throws IOException {     
        return Utils.encoding(extension_type, extension_data);
    }

    public static Extension parse(ByteBuffer buffer) {
        ExtensionType extension_type = ExtensionTypeImpl.parse(buffer);
        Vector<Byte> extension_data = Vector.parseOpaqueVector(
                buffer, EXTENSION_DATA_LENGTH_BYTES);
        
        return new ExtensionImpl(extension_type, extension_data);
    }

}
