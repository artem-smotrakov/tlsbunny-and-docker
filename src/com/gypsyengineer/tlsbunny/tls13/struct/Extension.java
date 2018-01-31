package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;

public class Extension implements Struct {
    
    public static final int EXTENSION_DATA_LENGTH_BYTES = 2;

    private ExtensionType extension_type;
    private Vector<Byte> extension_data;
    
    public Extension(ExtensionType extension_type, Vector<Byte> extension_data) {
        this.extension_type = extension_type;
        this.extension_data = extension_data;
    }
    
    public ExtensionType getExtensionType() {
        return extension_type;
    }
    
    public void setExtensionType(ExtensionType extension_type) {
        this.extension_type = extension_type;
    }
    
    public Vector<Byte> getExtensionData() {
        return extension_data;
    }
    
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
        ExtensionType extension_type = ExtensionType.parse(buffer);
        Vector<Byte> extension_data = Vector.parseOpaqueVector(
                buffer, EXTENSION_DATA_LENGTH_BYTES);
        
        return new Extension(extension_type, extension_data);
    }
    
    public static Extension wrap(SupportedVersions supportedVersions) throws IOException {
        return wrap(ExtensionType.supported_versions, supportedVersions.encoding());
    }
    
    public static Extension wrap(SignatureSchemeList signatureSchemeList) throws IOException {
        return wrap(ExtensionType.signature_algorithms, signatureSchemeList.encoding());
    }
    
    public static Extension wrap(NamedGroupList namedGroupList) throws IOException {
        return wrap(ExtensionType.supported_groups, namedGroupList.encoding());
    }
    
    public static Extension wrap(KeyShare keyShare) throws IOException {
        return wrap(ExtensionType.key_share, keyShare.encoding());
    }
    
    public static Extension wrap(ExtensionType type, byte[] bytes) {
        return new Extension(type, Vector.wrap(EXTENSION_DATA_LENGTH_BYTES, bytes));
    }

}
