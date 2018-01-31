package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;

public class TLSPlaintext implements Struct {
    
    public static final int MAX_ALLOWED_LENGTH = 16384;

    private ContentType type;
    private ProtocolVersion legacy_record_version;
    private UInt16 length;
    private Bytes fragment;

    TLSPlaintext(ContentType type, ProtocolVersion version, 
            UInt16 length, Bytes fragment) {
        
        this.type = type;
        this.legacy_record_version = version;
        this.length = length;
        this.fragment = fragment;
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(type, legacy_record_version, length, fragment);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(type, legacy_record_version, length, fragment);
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public void setLegacyRecordVersion(ProtocolVersion version) {
        this.legacy_record_version = version;
    }

    public void setLength(UInt16 length) {
        this.length = length;
    }

    public void setFragment(Bytes fragment) {
        this.fragment = fragment;
    }

    public ContentType getType() {
        return type;
    }

    public ProtocolVersion getLegacyRecordVersion() {
        return legacy_record_version;
    }

    public UInt16 getLength() {
        return length;
    }

    public byte[] getFragment() {
        return fragment.encoding();
    }

    public boolean containsHandshake() {
        return type.isHandshake();
    }

    public boolean containsApplicationData() {
        return type.isApplicationData();
    }

    public boolean containsAlert() {
        return type.isAlert();
    }

    public static TLSPlaintext parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }
    
    public static TLSPlaintext parse(ByteBuffer buffer) {
        ContentType type = ContentType.parse(buffer);
        ProtocolVersion legacy_record_version = ProtocolVersion.parse(buffer);
        UInt16 length = UInt16.parse(buffer);
        Bytes fragment = Bytes.parse(buffer, length.value);
        
        return new TLSPlaintext(type, legacy_record_version, length, fragment);
    }

}
