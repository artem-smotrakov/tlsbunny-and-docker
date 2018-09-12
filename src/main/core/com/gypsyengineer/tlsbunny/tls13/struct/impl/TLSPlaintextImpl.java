package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;

public class TLSPlaintextImpl implements TLSPlaintext {

    private final ContentType type;
    private final ProtocolVersion legacy_record_version;
    private final UInt16 length;
    private final Bytes fragment;

    TLSPlaintextImpl(ContentType type, ProtocolVersion version,
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
    public ContentType getType() {
        return type;
    }

    @Override
    public ProtocolVersion getLegacyRecordVersion() {
        return legacy_record_version;
    }

    @Override
    public UInt16 getFragmentLength() {
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

    @Override
    public boolean containsChangeCipherSpec() {
        return type.isChangeCipherSpec();
    }
}
