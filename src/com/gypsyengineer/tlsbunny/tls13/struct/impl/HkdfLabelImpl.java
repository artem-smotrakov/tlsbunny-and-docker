package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls13.struct.HkdfLabel;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;

public class HkdfLabelImpl implements HkdfLabel {

    private final UInt16 length;
    private final Vector<Byte> label;
    private final Vector<Byte> hash_value;

    HkdfLabelImpl(UInt16 length, Vector<Byte> label, Vector<Byte> hash_value) {
        this.length = length;
        this.label = label;
        this.hash_value = hash_value;
    }

    @Override
    public UInt16 getLength() {
        return length;
    }

    @Override
    public Vector<Byte> getLabel() {
        return label;
    }

    @Override
    public Vector<Byte> getContext() {
        return hash_value;
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(length, label, hash_value);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(length, label, hash_value);
    }

}
