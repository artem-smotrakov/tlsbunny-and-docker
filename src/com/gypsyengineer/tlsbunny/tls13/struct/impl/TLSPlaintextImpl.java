package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

public class TLSPlaintextImpl implements TLSPlaintext {

    private ContentTypeImpl type;
    private ProtocolVersionImpl legacy_record_version;
    private UInt16 length;
    private Bytes fragment;

    TLSPlaintextImpl(ContentTypeImpl type, ProtocolVersionImpl version, 
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

    @Override
    public void setType(ContentTypeImpl type) {
        this.type = type;
    }

    @Override
    public void setLegacyRecordVersion(ProtocolVersionImpl version) {
        this.legacy_record_version = version;
    }

    @Override
    public void setLength(UInt16 length) {
        this.length = length;
    }

    @Override
    public void setFragment(Bytes fragment) {
        this.fragment = fragment;
    }

    @Override
    public ContentTypeImpl getType() {
        return type;
    }

    @Override
    public ProtocolVersionImpl getLegacyRecordVersion() {
        return legacy_record_version;
    }

    @Override
    public UInt16 getLength() {
        return length;
    }

    @Override
    public byte[] getFragment() {
        return fragment.encoding();
    }

    @Override
    public boolean containsHandshake() {
        return type.isHandshake();
    }

    @Override
    public boolean containsApplicationData() {
        return type.isApplicationData();
    }

    @Override
    public boolean containsAlert() {
        return type.isAlert();
    }

    public static TLSPlaintextImpl parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }
    
    public static TLSPlaintextImpl parse(ByteBuffer buffer) {
        ContentTypeImpl type = ContentTypeImpl.parse(buffer);
        ProtocolVersionImpl legacy_record_version = ProtocolVersionImpl.parse(buffer);
        UInt16 length = UInt16.parse(buffer);
        Bytes fragment = Bytes.parse(buffer, length.value);
        
        return new TLSPlaintextImpl(type, legacy_record_version, length, fragment);
    }

}
