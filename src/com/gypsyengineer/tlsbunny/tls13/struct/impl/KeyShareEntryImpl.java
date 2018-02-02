package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.UncompressedPointRepresentation;

public class KeyShareEntryImpl implements KeyShareEntry {

    private NamedGroup group;
    private Vector<Byte> key_exchange;

    KeyShareEntryImpl(NamedGroup group, Vector<Byte> key_exchange) {
        this.group = group;
        this.key_exchange = key_exchange;
    }

    @Override
    public NamedGroup getNamedGroup() {
        return group;
    }

    @Override
    public void setNamedGroup(NamedGroup group) {
        this.group = group;
    }

    @Override
    public Vector<Byte> getKeyExchange() {
        return key_exchange;
    }

    @Override
    public void setKeyExchange(Vector<Byte> key_exchange) {
        this.key_exchange = key_exchange;
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(group, key_exchange);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(group, key_exchange);
    }

    public static KeyShareEntry wrap(NamedGroup group, UncompressedPointRepresentation upr) throws IOException {
        return new KeyShareEntryImpl(
                group, 
                Vector.wrap(KEY_EXCHANGE_LENGTH_BYTES, upr.encoding()));
    }
    
    public static KeyShareEntry wrap(NamedGroup group, byte[] bytes) {
        return new KeyShareEntryImpl(
                group, 
                Vector.wrap(KEY_EXCHANGE_LENGTH_BYTES, bytes));
    }

}
