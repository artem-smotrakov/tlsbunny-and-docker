package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.util.Objects;

import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;

public class KeyShareEntryImpl implements KeyShareEntry {

    private NamedGroup group;
    private Vector<Byte> key_exchange;

    KeyShareEntryImpl(NamedGroup group, Vector<Byte> key_exchange) {
        this.group = group;
        this.key_exchange = key_exchange;
    }

    @Override
    public NamedGroup namedGroup() {
        return group;
    }

    @Override
    public KeyShareEntry keyExchange(Vector<Byte> bytes) {
        key_exchange = bytes;
        return this;
    }

    @Override
    public KeyShareEntry namedGroup(NamedGroup group) {
        this.group = group;
        return this;
    }

    @Override
    public Vector<Byte> keyExchange() {
        return key_exchange;
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(group, key_exchange);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(group, key_exchange);
    }

    @Override
    public KeyShareEntryImpl copy() {
        return new KeyShareEntryImpl(
                (NamedGroup) group.copy(),
                (Vector<Byte>) key_exchange.copy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeyShareEntryImpl that = (KeyShareEntryImpl) o;
        return Objects.equals(group, that.group) &&
                Objects.equals(key_exchange, that.key_exchange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, key_exchange);
    }
}
