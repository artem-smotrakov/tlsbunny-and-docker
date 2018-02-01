package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.HkdfLabel;

public class HkdfLabelImpl implements HkdfLabel {


    private UInt16 length;
    private Vector<Byte> label;
    private Vector<Byte> hash_value;

    public HkdfLabelImpl(UInt16 length, Vector<Byte> label, Vector<Byte> hash_value) {
        this.length = length;
        this.label = label;
        this.hash_value = hash_value;
    }

    @Override
    public UInt16 getLength() {
        return length;
    }

    @Override
    public void setLength(UInt16 length) {
        this.length = length;
    }

    @Override
    public Vector<Byte> getLabel() {
        return label;
    }

    @Override
    public void setLabel(Vector<Byte> label) {
        this.label = label;
    }

    @Override
    public Vector<Byte> getHashValue() {
        return hash_value;
    }

    @Override
    public void setHashValue(Vector<Byte> hash_value) {
        this.hash_value = hash_value;
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(length, label, hash_value);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(length, label, hash_value);
    }

    public static HkdfLabelImpl create(int length, byte[] label, byte[] hashValue) {
        return new HkdfLabelImpl(
                new UInt16(length),
                Vector.wrap(LABEL_LENGTH_BYTES, label),
                Vector.wrap(HASH_VALUE_LENGTH_BYTES, hashValue));
    }

}
