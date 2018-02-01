package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupListImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.KeyShareImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SignatureSchemeListImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.SupportedVersionsImpl;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.SupportedVersions;

public class ExtensionImpl implements Extension {
    

    private ExtensionTypeImpl extension_type;
    private Vector<Byte> extension_data;
    
    public ExtensionImpl(ExtensionTypeImpl extension_type, Vector<Byte> extension_data) {
        this.extension_type = extension_type;
        this.extension_data = extension_data;
    }
    
    @Override
    public ExtensionTypeImpl getExtensionType() {
        return extension_type;
    }
    
    @Override
    public void setExtensionType(ExtensionTypeImpl extension_type) {
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

    public static ExtensionImpl parse(ByteBuffer buffer) {
        ExtensionTypeImpl extension_type = ExtensionTypeImpl.parse(buffer);
        Vector<Byte> extension_data = Vector.parseOpaqueVector(
                buffer, EXTENSION_DATA_LENGTH_BYTES);
        
        return new ExtensionImpl(extension_type, extension_data);
    }
    
    public static ExtensionImpl wrap(SupportedVersions supportedVersions) throws IOException {
        return wrap(ExtensionTypeImpl.supported_versions, supportedVersions.encoding());
    }
    
    public static ExtensionImpl wrap(SignatureSchemeListImpl signatureSchemeList) throws IOException {
        return wrap(ExtensionTypeImpl.signature_algorithms, signatureSchemeList.encoding());
    }
    
    public static ExtensionImpl wrap(NamedGroupListImpl namedGroupList) throws IOException {
        return wrap(ExtensionTypeImpl.supported_groups, namedGroupList.encoding());
    }
    
    public static ExtensionImpl wrap(KeyShareImpl keyShare) throws IOException {
        return wrap(ExtensionTypeImpl.key_share, keyShare.encoding());
    }
    
    public static ExtensionImpl wrap(ExtensionTypeImpl type, byte[] bytes) {
        return new ExtensionImpl(type, Vector.wrap(EXTENSION_DATA_LENGTH_BYTES, bytes));
    }

}
