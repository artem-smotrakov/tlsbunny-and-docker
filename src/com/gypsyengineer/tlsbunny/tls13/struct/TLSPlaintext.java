package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class TLSPlaintext implements Entity {
    
    public static final int MAX_ALLOWED_LENGTH = 16384;

    private ContentType type;
    private ProtocolVersion legacy_record_version;
    private UInt16 length;
    private Entity fragment;

    public TLSPlaintext() {
        this(ContentType.HANDSHAKE, ProtocolVersion.TLSv10, UInt16.ZERO, Bytes.EMPTY);
    }
    
    public TLSPlaintext(
            ContentType type, ProtocolVersion version, byte[] fragment) {
        
        this(type, version, new UInt16(fragment.length), new Bytes(fragment));
    }

    public TLSPlaintext(
            ContentType type, ProtocolVersion version, UInt16 length, Entity fragment) {
        
        this.type = type;
        this.legacy_record_version = version;
        this.length = length;
        this.fragment = fragment;
    }

    public TLSPlaintext(
            ContentType type, ProtocolVersion version, UInt16 length, byte[] fragment) {
        
        this(type, version, length, new Bytes(fragment));
    }

    @Override
    public int encodingLength() {
        return type.encodingLength() 
                + legacy_record_version.encodingLength()
                + length.encodingLength() 
                + fragment.encodingLength();
    }

    @Override
    public byte[] encoding() throws IOException {
        return ByteBuffer.allocate(encodingLength())
                .put(type.encoding())
                .put(legacy_record_version.encoding())
                .put(length.encoding())
                .put(fragment.encoding())
                .array();
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

    public void setFragment(byte[] fragment) {
        this.fragment = new Bytes(fragment);
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

    public byte[] getFragment() throws IOException {
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
        byte[] fragment = new byte[length.value];
        buffer.get(fragment);

        return new TLSPlaintext(type, legacy_record_version, length, fragment);
    }
    
    public static TLSPlaintext[] wrap(
            ContentType type, ProtocolVersion version, byte[] encoded) {
        
        if (encoded.length <= MAX_ALLOWED_LENGTH) {
            return new TLSPlaintext[] {
                new TLSPlaintext(type, version, encoded)
            };
        }

        List<byte[]> fragments = Utils.split(encoded, MAX_ALLOWED_LENGTH);
        TLSPlaintext[] tlsPlaintexts = new TLSPlaintext[fragments.size()];
        for (int i=0; i < fragments.size(); i++) {
            tlsPlaintexts[i] = new TLSPlaintext(type, version, fragments.get(i));
        }

        return tlsPlaintexts;
    }

}
