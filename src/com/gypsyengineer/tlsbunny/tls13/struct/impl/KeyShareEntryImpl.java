package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;

public class KeyShareEntryImpl implements KeyShareEntry {


    private NamedGroupImpl group;
    private Vector<Byte> key_exchange;

    private KeyShareEntryImpl(NamedGroupImpl group, Vector<Byte> key_exchange) {
        this.group = group;
        this.key_exchange = key_exchange;
    }

    @Override
    public NamedGroupImpl getNamedGroup() {
        return group;
    }

    @Override
    public void setNamedGroup(NamedGroupImpl group) {
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

    public static KeyShareEntryImpl parse(ByteBuffer buffer) {
        NamedGroupImpl group = NamedGroupImpl.parse(buffer);
        Vector<Byte> key_exchange = Vector.parseOpaqueVector(
                buffer, KEY_EXCHANGE_LENGTH_BYTES);
        
        return new KeyShareEntryImpl(group, key_exchange);
    }
    
    public static KeyShareEntryImpl wrap(NamedGroupImpl group, UncompressedPointRepresentationImpl upr) {
        return new KeyShareEntryImpl(
                group, 
                Vector.wrap(KEY_EXCHANGE_LENGTH_BYTES, upr.encoding()));
    }
    
    public static KeyShareEntryImpl wrap(NamedGroupImpl group, byte[] bytes) {
        return new KeyShareEntryImpl(
                group, 
                Vector.wrap(KEY_EXCHANGE_LENGTH_BYTES, bytes));
    }

}
