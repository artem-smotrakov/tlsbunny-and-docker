package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class KeyShareEntry implements Entity {

    public static final int KEY_EXCHANGE_LENGTH_BYTES = 2;
    public static final int MIN_EXPECTED_KEY_EXCHANGE_BYTES = 1;
    public static final int MAX_EXPECTED_KEY_EXCHANGE_BYTES = 65535;

    private NamedGroup group;
    private Vector<Byte> key_exchange;

    private KeyShareEntry(NamedGroup group, Vector<Byte> key_exchange) {
        this.group = group;
        this.key_exchange = key_exchange;
    }

    public NamedGroup getNamedGroup() {
        return group;
    }

    public void setNamedGroup(NamedGroup group) {
        this.group = group;
    }

    public Vector<Byte> getKeyExchange() {
        return key_exchange;
    }

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
        NamedGroup group = NamedGroup.parse(buffer);
        Vector<Byte> key_exchange = Vector.parseOpaqueVector(
                buffer, KEY_EXCHANGE_LENGTH_BYTES);
        
        return new KeyShareEntry(group, key_exchange);
    }
    
    public static KeyShareEntry wrap(NamedGroup group, UncompressedPointRepresentation upr) {
        return new KeyShareEntry(
                group, 
                Vector.wrap(KEY_EXCHANGE_LENGTH_BYTES, upr.encoding()));
    }
    
    public static KeyShareEntry wrap(NamedGroup group, byte[] bytes) {
        return new KeyShareEntry(
                group, 
                Vector.wrap(KEY_EXCHANGE_LENGTH_BYTES, bytes));
    }

}
