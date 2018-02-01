package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.*; // TODO

public class KeyShareEntryImpl implements KeyShareEntry {

    private NamedGroup group;
    private Vector<Byte> key_exchange;

    private KeyShareEntryImpl(NamedGroup group, Vector<Byte> key_exchange) {
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

    public static KeyShareEntry parse(ByteBuffer buffer) {
        NamedGroup group = NamedGroupImpl.parse(buffer);
        Vector<Byte> key_exchange = Vector.parseOpaqueVector(
                buffer, KEY_EXCHANGE_LENGTH_BYTES);
        
        return new KeyShareEntryImpl(group, key_exchange);
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
