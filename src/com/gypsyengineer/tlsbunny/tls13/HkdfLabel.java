package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;

public class HkdfLabel implements Entity {

    public static final int LABEL_LENGTH_BYTES = 1;
    public static final int HASH_VALUE_LENGTH_BYTES = 1;

    public static final int MIN_LABEL_LENGTH = 7;
    public static final int MAX_LABEL_LENGTH = 255;
    public static final int MIN_HASH_VALUE_LENGTH = 0;
    public static final int MAX_HASH_VALUE_LENGTH = 255;

    private UInt16 length;
    private Vector<Byte> label;
    private Vector<Byte> hash_value;

    public HkdfLabel(UInt16 length, Vector<Byte> label, Vector<Byte> hash_value) {
        this.length = length;
        this.label = label;
        this.hash_value = hash_value;
    }

    public UInt16 getLength() {
        return length;
    }

    public void setLength(UInt16 length) {
        this.length = length;
    }

    public Vector<Byte> getLabel() {
        return label;
    }

    public void setLabel(Vector<Byte> label) {
        this.label = label;
    }

    public Vector<Byte> getHashValue() {
        return hash_value;
    }

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

    public static HkdfLabel create(int length, byte[] label, byte[] hashValue) {
        return new HkdfLabel(
                new UInt16(length),
                Vector.wrap(LABEL_LENGTH_BYTES, label),
                Vector.wrap(HASH_VALUE_LENGTH_BYTES, hashValue));
    }

}
