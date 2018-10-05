package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroupList;
import java.io.IOException;
import java.util.Objects;

public class NamedGroupListImpl implements NamedGroupList {

    private final Vector<NamedGroup> named_group_list;

    NamedGroupListImpl(Vector<NamedGroup> named_group_list) {
        this.named_group_list = named_group_list;
    }

    @Override
    public int encodingLength() {
        return named_group_list.encodingLength();
    }

    @Override
    public byte[] encoding() throws IOException {
        return named_group_list.encoding();
    }

    @Override
    public NamedGroupListImpl copy() {
        return new NamedGroupListImpl((Vector<NamedGroup>) named_group_list.copy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NamedGroupListImpl that = (NamedGroupListImpl) o;
        return Objects.equals(named_group_list, that.named_group_list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(named_group_list);
    }
}
